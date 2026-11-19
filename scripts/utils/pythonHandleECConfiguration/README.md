# Configurazione enti creditori da Excel

Lo script [handle_ec_configuration.py](./handle_ec_configuration.py) legge le azioni da un file `.xlsx`, chiama le API di configurazione pagoPA e produce un report con gli esiti.

## Installazione

Usa Python 3.11 o successivo. Un ambiente virtuale non è obbligatorio, ma è consigliato per tenere separate le dipendenze dello script. Dalla radice del repository, su macOS o Linux:

```bash
cd scripts/utils/pythonHandleECConfiguration
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
```

Quando riapri il terminale, basta tornare in questa cartella e riattivare `.venv`.

## Avvio

Imposta l'ambiente e la relativa subscription key. Per `DEV`, ad esempio:

```bash
export APICONFIG_ENV=DEV
export APICONFIG_SUBKEY_DEV='<subscription-key DEV>'
python handle_ec_configuration.py ./enti.xlsx
```

Sostituisci `./enti.xlsx` con il percorso del tuo file. Se ometti il percorso, lo script cerca [excel_example.xlsx](./excel_example.xlsx) nella cartella corrente.

`APICONFIG_ENV` vale `DEV` per impostazione predefinita. Per `UAT` o `PROD`, imposta `APICONFIG_ENV` di conseguenza e usa rispettivamente `APICONFIG_SUBKEY_UAT` o `APICONFIG_SUBKEY_PROD`. Non salvare le chiavi nel repository.

Lo script effettua operazioni reali nell'ambiente selezionato. Per una prova senza chiamate API, usa `Nessuna azione` su tutte le righe. La verifica del certificato TLS è attualmente disabilitata nello script (`VERIFY_SSL = False`); abilitala prima di usare credenziali reali.

## File di input

Lo script legge il primo foglio, con le intestazioni alla riga 2. Nella colonna `Azione` usa una delle seguenti descrizioni, anche se il menu del foglio mostra voci diverse:

- `Aggiornamento IBAN`
- `Aggiornamento Stazione`
- `Aggiornamento IBAN e Stazione`
- `Nuovo Censimento Ente e IBAN`
- `Nuovo Censimento IBAN`
- `Nessuna azione`

Per `Nuovo Censimento Ente e IBAN` indica anche la `Stazione`: lo script censisce l'ente, crea la relazione con la stazione e poi registra l'IBAN.

## Report e retry

Lo script salva `report_<timestamp>.xlsx` nella cartella corrente. Controlla `Esito`, `Motivazione Errore` e `Dettaglio Azioni`. Dopo aver corretto le righe in errore, puoi rilanciare il report nello **stesso ambiente**:

```bash
python handle_ec_configuration.py ./report_20261119_111303.xlsx
```

Le righe già `OK` vengono conservate senza chiamate API. Le righe `KO` ripartono dallo step fallito, saltando quelli già riusciti. Ad esempio, se fallisce la relazione con la stazione dopo il censimento dell'ente, il retry riprova la relazione e poi registra l'IBAN.

Non modificare `Motivazione Errore` o `Dettaglio Azioni`: servono per individuare lo step fallito. Se il report non contiene alcun dettaglio (ad esempio per un'azione non valida), la riga riparte dall'inizio. Se cambi l'azione o i dati di uno step già riuscito, svuota `Esito` per rieseguire la riga dall'inizio.
