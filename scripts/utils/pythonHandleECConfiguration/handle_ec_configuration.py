"""
Elabora un file Excel di enti creditori ed esegue, per ogni riga, le azioni
indicate nella colonna "Azione":

    A - Aggiornamento IBAN: registra il nuovo IBAN
    B - Aggiornamento Stazione: crea la relazione con la stazione indicata
    C - Aggiornamento IBAN e Stazione: crea la relazione, poi registra l'IBAN
    D - Nuovo Censimento Ente e IBAN: censisce l'ente, lo collega alla stazione indicata
        nella colonna "Stazione", poi registra l'IBAN
    E - Nuovo Censimento IBAN: registra l'IBAN
    F - Nessuna azione: non effettua chiamate

Non vengono rimossi gli IBAN o i collegamenti preesistenti. Non occorre
compilare "Operazione IBAN". L'IBAN e' attivo, valido da domani e,
in assenza di una scadenza esplicita, scade tra 365 giorni.
Al primo errore si passa alla riga successiva.

Al termine viene generato un file Excel di report con l'esito (OK/KO) evidenziato
e la motivazione dell'errore. Per un retry, passare il report come input:
le righe con Esito OK sono conservate senza ripetere le chiamate API.
Le righe KO ripartono dallo step fallito, se il report lo identifica.

Uso:
    python -m pip install -r requirements.txt
    python handle_ec_configuration.py [file_input.xlsx]
"""

import json
import os
import re
import sys
import time
from datetime import date, datetime, timedelta
from urllib.parse import quote

import pandas as pd
import requests
import urllib3
from openpyxl import Workbook
from openpyxl.styles import Alignment, Font, PatternFill
from openpyxl.utils import get_column_letter

# =============================================================================
# 1. CONFIGURAZIONE
# =============================================================================

# Ambiente attivo: DEV | UAT | PROD
ENV = os.getenv("APICONFIG_ENV", "DEV")

ENVIRONMENTS = {
    "DEV": {
        "base_url": "https://api.dev.platform.pagopa.it/apiconfig/api/v1",
        "subscription_key": os.getenv("APICONFIG_SUBKEY_DEV", ""),
    },
    "UAT": {
        "base_url": "https://api.uat.platform.pagopa.it/apiconfig/api/v1",
        "subscription_key": os.getenv("APICONFIG_SUBKEY_UAT", ""),
    },
    "PROD": {
        "base_url": "https://api.platform.pagopa.it/apiconfig/api/v1",
        "subscription_key": os.getenv("APICONFIG_SUBKEY_PROD", ""),
    },
}

BASE_URL = ENVIRONMENTS[ENV]["base_url"]
SUBSCRIPTION_KEY = ENVIRONMENTS[ENV]["subscription_key"]

URL_CREATE_CI = f"{BASE_URL}/creditorinstitutions"
URL_CREATE_CI_STATION = f"{BASE_URL}/creditorinstitutions/{{creditorinstitutioncode}}/stations"
URL_CREATE_IBAN = f"{BASE_URL}/creditorinstitutions/{{creditorinstitutioncode}}/ibans"

VERIFY_SSL = False
REQUEST_TIMEOUT = 60  # secondi
SLEEP_BETWEEN_CALLS = 0.5  # secondi

# File di input/output
FILE_PATH = sys.argv[1] if len(sys.argv) > 1 else "excel_example.xlsx"
SHEET_NAME = 0  # primo foglio ("Enti Aggregati")
HEADER_ROW = 1  # indice 0-based: l'intestazione e' alla riga 2 di Excel
REPORT_SHEET = "Report"
REPORT_PATH = f"report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.xlsx"

# Codici azione
ACTIONS = {
    "A": "Aggiornamento IBAN",
    "B": "Aggiornamento Stazione",
    "C": "Aggiornamento IBAN e Stazione",
    "D": "Nuovo Censimento Ente e IBAN",
    "E": "Nuovo Censimento IBAN",
    "F": "Nessuna azione",
}
ACTION_CODES = {description.casefold(): code for code, description in ACTIONS.items()}

# Default censimento ente
PROVINCIA_FALLBACK = "IT"

STATION_AUX_DIGIT = 3
STATION_APPLICATION_CODE = None
STATION_SEGREGATION_CODE = 0  # "00" nel formato a due cifre; l'API richiede un intero.
STATION_DEFAULT_FLAGS = {
    "mod4": False,
    "broadcast": False,
    "aca": True,
    "stand_in": True,
    "spontaneous_payment": False,
}

IBAN_DESCRIPTION = "IBAN"
IBAN_ACTIVATION_DAYS_OFFSET = 1
IBAN_EXPIRATION_DAYS_OFFSET = 365

# Nomi colonne Excel (normalizzati: spazi multipli/finali rimossi)
COL_RAGIONE_SOCIALE = "Ragione Sociale"
COL_CODICE_FISCALE = "Codice Fiscale"
COL_INDIRIZZO = "Sede legale - Indirizzo"
COL_CITTA = "Sede legale - Città"
COL_PROVINCIA = "Sede legale - Provincia (sigla)"
COL_CAP = "CAP"
COL_IBAN = "IBAN"
COL_STAZIONE = "Stazione"
COL_ATTIVO = "Attivo SI/NO"
COL_AZIONE = "Azione"
COL_AUX_DIGIT = "Aux Digit"  # opzionale
COL_APPLICATION_CODE = "Application Code"  # opzionale
COL_IBAN_DESCRIPTION = "Descrizione IBAN"
COL_IBAN_DUE_DATE = "Data scadenza IBAN"

COL_ESITO = "Esito"
COL_MOTIVAZIONE = "Motivazione Errore"
COL_DETTAGLIO = "Dettaglio Azioni"
DUPLICATE_REASON = "Ente Duplicato nel file"
DUPLICATE_RETRY_PREFIX = "Retry precedente: "

# =============================================================================
# 2. UTILITY
# =============================================================================

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

HEADERS_AUTH = {"Ocp-Apim-Subscription-Key": SUBSCRIPTION_KEY}
HEADERS_JSON = {**HEADERS_AUTH, "Content-Type": "application/json"}


class StepError(Exception):
    pass


def normalize_col(name):
    return re.sub(r"\s+", " ", str(name)).strip()


def cell(row, col):
    """Restituisce il valore della cella come stringa pulita o None."""
    if col not in row.index:
        return None
    value = row[col]
    if pd.isna(value):
        return None
    if isinstance(value, float) and value.is_integer():
        value = int(value)
    value = str(value).strip()
    return value or None


def to_int(value):
    if value is None:
        return None
    try:
        return int(float(value))
    except ValueError:
        raise StepError(f"valore numerico non valido: '{value}'")


def parse_action(raw):
    if raw is None:
        code = ACTION_CODES.get("F")  # default action code if raw is None
    else:
        code = ACTION_CODES.get(normalize_col(raw).casefold())
    return code


def error_detail(response):
    """Estrae un messaggio leggibile da una risposta di errore (ProblemJson)."""
    try:
        body = response.json()
        if isinstance(body, dict):
            detail = body.get("detail") or body.get("title") or body.get("message")
            if detail:
                return f"HTTP {response.status_code} - {detail}"
        return f"HTTP {response.status_code} - {body}"
    except ValueError:
        text = (response.text or "").strip()
        return f"HTTP {response.status_code}" + (f" - {text[:500]}" if text else "")


def get_fiscal_code(row):
    cf = cell(row, COL_CODICE_FISCALE)
    if cf is None:
        raise StepError("Codice Fiscale mancante")
    if len(cf) == 10 and cf.isdigit():
        cf = "0" + cf
    return cf


# =============================================================================
# 3. AZIONI
# =============================================================================

def action_create_ci(row):
    cf = get_fiscal_code(row)
    ragione_sociale = cell(row, COL_RAGIONE_SOCIALE)
    if ragione_sociale is None:
        raise StepError("Ragione Sociale mancante")

    citta = cell(row, COL_CITTA) or "-"
    indirizzo = cell(row, COL_INDIRIZZO) or "-"

    provincia = PROVINCIA_FALLBACK
    prov = cell(row, COL_PROVINCIA)
    if prov:
        prov = prov.upper()
        if len(prov) == 2 and prov.isalpha():
            provincia = prov

    cap = cell(row, COL_CAP)
    cap = cap.zfill(5) if cap else "00000"

    attivo = cell(row, COL_ATTIVO)
    enabled = True if attivo is None else attivo.upper() in ("SI", "SÌ", "S", "YES", "Y", "TRUE", "1")

    payload = {
        "creditor_institution_code": cf,
        "enabled": enabled,
        "business_name": ragione_sociale,
        "address": {
            "city": citta,
            "country_code": provincia,
            "location": indirizzo,
            "tax_domicile": citta,
            "zip_code": cap,
        },
        "psp_payment": True,
        "reporting_ftp": False,
        "reporting_zip": False,
    }

    response = requests.post(
        URL_CREATE_CI, headers=HEADERS_JSON, json=payload, verify=VERIFY_SSL, timeout=REQUEST_TIMEOUT
    )
    if response.status_code == 201:
        return "creato"
    if response.status_code == 409:
        return "già presente"
    raise StepError(error_detail(response))


def action_create_ci_station(row):
    cf = get_fiscal_code(row)
    station_code = cell(row, COL_STAZIONE)
    if station_code is None:
        raise StepError("Stazione mancante")

    aux_digit = to_int(cell(row, COL_AUX_DIGIT))
    aux_digit = STATION_AUX_DIGIT if aux_digit is None else aux_digit
    if aux_digit not in (0, 3):
        raise StepError("Aux Digit deve essere 0 o 3 con Codice Segregazione fisso a 00")

    application_code = to_int(cell(row, COL_APPLICATION_CODE))
    if application_code is None:
        application_code = STATION_APPLICATION_CODE

    payload = {
        "station_code": station_code,
        "aux_digit": aux_digit,
        "segregation_code": STATION_SEGREGATION_CODE,
        **STATION_DEFAULT_FLAGS,
    }
    if application_code is not None:
        payload["application_code"] = application_code

    response = requests.post(
        URL_CREATE_CI_STATION.format(creditorinstitutioncode=quote(cf, safe="")),
        headers=HEADERS_JSON,
        json=payload,
        verify=VERIFY_SSL,
        timeout=REQUEST_TIMEOUT,
    )
    if response.status_code == 201:
        return f"relazione con {station_code} creata"
    if response.status_code == 409:
        return f"relazione con {station_code} già presente"
    raise StepError(error_detail(response))


def get_iban_due_date(row):
    value = cell(row, COL_IBAN_DUE_DATE)
    if value is None:
        return None
    # pandas puo' rappresentare una data Excel come un timestamp a mezzanotte.
    value = value.removesuffix(" 00:00:00")
    try:
        parsed = date.fromisoformat(value)
    except ValueError:
        raise StepError(f"{COL_IBAN_DUE_DATE} non valida: usare il formato AAAA-MM-GG") from None
    if parsed.isoformat() != value:
        raise StepError(f"{COL_IBAN_DUE_DATE} non valida: usare il formato AAAA-MM-GG")
    return parsed


def action_create_iban(row):
    cf = get_fiscal_code(row)
    if not re.fullmatch(r"[0-9]{11}", cf):
        raise StepError("Il Codice Fiscale dell'ente deve avere 11 cifre per registrare l'IBAN")
    iban = cell(row, COL_IBAN)
    if iban is None:
        raise StepError("IBAN mancante")
    iban = "".join(iban.split()).upper()
    if not re.fullmatch(r"[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}", iban):
        raise StepError("Formato IBAN non valido")
    today = date.today()
    validity_date = today + timedelta(days=IBAN_ACTIVATION_DAYS_OFFSET)
    due_date = get_iban_due_date(row) or (today + timedelta(days=IBAN_EXPIRATION_DAYS_OFFSET))
    if due_date <= validity_date:
        raise StepError(f"{COL_IBAN_DUE_DATE} deve essere successiva alla data di attivazione")

    payload = {
        "iban": iban,
        "description": cell(row, COL_IBAN_DESCRIPTION) or IBAN_DESCRIPTION,
        "is_active": True,
        "validity_date": f"{validity_date.isoformat()}T00:00:00.000Z",
        "due_date": f"{due_date.isoformat()}T00:00:00.000Z",
    }
    response = requests.post(
        URL_CREATE_IBAN.format(creditorinstitutioncode=cf),
        headers=HEADERS_JSON,
        json=payload,
        verify=VERIFY_SSL,
        timeout=REQUEST_TIMEOUT,
    )
    if response.status_code == 201:
        return "IBAN registrato"
    raise StepError(error_detail(response))


ACTION_STEPS = {
    "A": (("IBAN", action_create_iban),),
    "B": (("Stazione", action_create_ci_station),),
    "C": (("Stazione", action_create_ci_station), ("IBAN", action_create_iban)),
    "D": (
        ("Ente", action_create_ci),
        ("Stazione", action_create_ci_station),
        ("IBAN", action_create_iban),
    ),
    "E": (("IBAN", action_create_iban),),
    "F": (),
}

# =============================================================================
# 4. CARICAMENTO EXCEL
# =============================================================================

def load_excel(path):
    if not os.path.exists(path):
        sys.exit(f"❌ ERRORE: il file '{path}' non è stato trovato.")

    print(f"📂 Leggo il file Excel: {path}...")
    # dtype=str + keep_default_na=False: preserva "NA" (Napoli), zeri iniziali di CF/CAP
    # e IBAN; solo le celle vuote diventano NaN.
    with pd.ExcelFile(path) as workbook:
        is_report = REPORT_SHEET in workbook.sheet_names
        header_row = 0 if is_report else HEADER_ROW
        df = workbook.parse(
            sheet_name=REPORT_SHEET if is_report else SHEET_NAME,
            header=header_row,
            dtype=str,
            keep_default_na=False,
            na_values=[""],
        )
    df.columns = [normalize_col(c) for c in df.columns]
    df = df.loc[:, ~df.columns.str.startswith("Unnamed")]
    df.index += header_row + 2
    df = df.dropna(how="all")

    required = [COL_CODICE_FISCALE, COL_AZIONE]
    if is_report:
        required.append(COL_ESITO)
    missing = [c for c in required if c not in df.columns]
    if missing:
        sys.exit(f"❌ ERRORE: colonne mancanti {missing}. Colonne rilevate: {df.columns.tolist()}")

    print(f"✅ File caricato. Trovate {len(df)} righe.")
    return df


# =============================================================================
# 5. REPORT
# =============================================================================

FILL_OK = PatternFill(start_color="C6EFCE", end_color="C6EFCE", fill_type="solid")
FILL_KO = PatternFill(start_color="FFC7CE", end_color="FFC7CE", fill_type="solid")
FONT_OK = Font(bold=True, color="006100")
FONT_KO = Font(bold=True, color="9C0006")
FILL_HEADER = PatternFill(start_color="1F4E78", end_color="1F4E78", fill_type="solid")
FONT_HEADER = Font(bold=True, color="FFFFFF")


def write_report(df, path):
    wb = Workbook()
    ws = wb.active
    ws.title = REPORT_SHEET

    columns = df.columns.tolist()
    ws.append(columns)
    for c in ws[1]:
        c.fill = FILL_HEADER
        c.font = FONT_HEADER
        c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)

    esito_idx = columns.index(COL_ESITO) + 1
    for values in df.itertuples(index=False):
        ws.append(["" if pd.isna(v) else v for v in values])
        esito_cell = ws.cell(row=ws.max_row, column=esito_idx)
        ok = esito_cell.value == "OK"
        esito_cell.fill = FILL_OK if ok else FILL_KO
        esito_cell.font = FONT_OK if ok else FONT_KO
        esito_cell.alignment = Alignment(horizontal="center")

    for idx, col in enumerate(columns, start=1):
        width = max([len(str(col))] + [len(str(v)) for v in df[col].fillna("")])
        ws.column_dimensions[get_column_letter(idx)].width = min(max(width + 2, 10), 80)
    ws.freeze_panes = "A2"
    ws.auto_filter.ref = ws.dimensions

    legend = wb.create_sheet("Legenda Azioni")
    legend.append(["Codice", "Azione"])
    for c in legend[1]:
        c.fill = FILL_HEADER
        c.font = FONT_HEADER
    for code, description in ACTIONS.items():
        legend.append([code, description])
    legend.column_dimensions["B"].width = 40

    wb.save(path)


# =============================================================================
# 6. ELABORAZIONE
# =============================================================================

def process_row(row):
    """Esegue le azioni della riga. Ritorna (esito, motivazione, dettaglio)."""
    details = []
    if cell(row, COL_AZIONE) is None:
        code = "F"
    else:
        try:
            code = parse_action(cell(row, COL_AZIONE))
        except StepError as e:
            return "KO", str(e), ""

    steps = ACTION_STEPS[code]
    if not steps:
        return "OK", "", f"{code}: {ACTIONS[code]}"

    start = 0
    previous_detail = cell(row, COL_DETTAGLIO)
    if (cell(row, COL_ESITO) or "").upper() == "KO" and previous_detail:
        previous_reason = cell(row, COL_MOTIVAZIONE) or ""
        if previous_reason == DUPLICATE_REASON and previous_detail.startswith(DUPLICATE_RETRY_PREFIX):
            try:
                previous = json.loads(previous_detail[len(DUPLICATE_RETRY_PREFIX):])
                previous_reason = previous["motivazione"]
                previous_detail = previous["dettaglio"]
                if not isinstance(previous_reason, str) or not isinstance(previous_detail, str):
                    raise ValueError("dati di retry non validi")
            except (ValueError, KeyError, TypeError):
                return "KO", "Dettaglio del retry duplicato non valido", previous_detail
        previous_steps = previous_detail.split(" | ")
        for index, (label, _) in enumerate(steps):
            entry = previous_steps[index] if index < len(previous_steps) else ""
            if entry.startswith(f"{label}: OK (") and entry.endswith(")"):
                details.append(entry)
                continue
            break
        if entry != f"{label}: KO" or not previous_reason.startswith(
            f"[{code} - {ACTIONS[code]} / {label}] "
        ):
            return (
                "KO",
                "Dettaglio del retry non coerente con l'azione o con lo step fallito. "
                "Per ripartire dall'inizio, svuotare Esito.",
                previous_detail,
            )
        start = index

    for index, (label, handler) in enumerate(steps[start:], start=start):
        try:
            outcome = handler(row)
            details.append(f"{label}: OK ({outcome})")
        except (StepError, requests.RequestException) as e:
            details.append(f"{label}: KO")
            skipped = [step_label for step_label, _ in steps[index + 1:]]
            if skipped:
                details.append(f"non eseguite: {', '.join(skipped)}")
            return "KO", f"[{code} - {ACTIONS[code]} / {label}] {e}", " | ".join(details)
        finally:
            time.sleep(SLEEP_BETWEEN_CALLS)

    return "OK", "", " | ".join(details)


def main():
    print(f"🌍 Ambiente: {ENV}")
    print(f"   - {URL_CREATE_CI}")
    print(f"   - {URL_CREATE_CI_STATION}")
    print(f"   - {URL_CREATE_IBAN}")
    print("🔑 Codici azione: " + ", ".join(f"{k}={v}" for k, v in ACTIONS.items()) + "\n")

    df = load_excel(FILE_PATH)
    results = []
    skipped_ok = 0
    seen_codes = set()

    print("🚀 Inizio elaborazione...\n")
    for excel_row, row in df.iterrows():
        label = cell(row, COL_RAGIONE_SOCIALE) or cell(row, COL_CODICE_FISCALE) or "N/A"
        cf = get_fiscal_code(row)
        if (cell(row, COL_ESITO) or "").upper() == "OK":
            seen_codes.add(cf)
            results.append(("OK", cell(row, COL_MOTIVAZIONE) or "", cell(row, COL_DETTAGLIO) or ""))
            skipped_ok += 1
            print(f"⏭️ [riga {excel_row}] {label}: già OK, saltata")
            continue

        if cf in seen_codes:
            detail = cell(row, COL_DETTAGLIO) or ""
            reason = cell(row, COL_MOTIVAZIONE) or ""
            if detail and reason and reason != DUPLICATE_REASON:
                detail = DUPLICATE_RETRY_PREFIX + json.dumps(
                    {"motivazione": reason, "dettaglio": detail}, ensure_ascii=False
                )
            results.append(("KO", DUPLICATE_REASON, detail))
            print(f"❌ [riga {excel_row}] {label}: KO {DUPLICATE_REASON}")
            continue

        seen_codes.add(cf)

        try:
            esito, motivazione, dettaglio = process_row(row)
        except Exception as e:  # salvaguardia: un errore inatteso non deve bloccare le altre righe
            esito, motivazione, dettaglio = "KO", f"errore script: {e}", ""

        icon = "✅" if esito == "OK" else "❌"
        print(f"{icon} [riga {excel_row}] {label}: {esito} {dettaglio} {motivazione}".rstrip())
        results.append((esito, motivazione, dettaglio))

    df[COL_ESITO] = [r[0] for r in results]
    df[COL_MOTIVAZIONE] = [r[1] for r in results]
    df[COL_DETTAGLIO] = [r[2] for r in results]
    write_report(df, REPORT_PATH)

    ok = sum(1 for r in results if r[0] == "OK")
    print("\n" + "=" * 40)
    print(f"📊 REPORT: OK {ok} (già OK e saltate: {skipped_ok}) | KO {len(results) - ok} | Totale {len(results)}")
    print(f"📄 Report salvato in: {REPORT_PATH}")


if __name__ == "__main__":
    main()
