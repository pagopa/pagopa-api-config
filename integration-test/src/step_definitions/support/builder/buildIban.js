function buildIbanCreate(iban) {
    today = new Date()
    today.setYear(today.getYear() + 1901)
    return {
        "description": "Testing",
        "iban": iban,
        "is_active": true,
        "labels": [
          {
            "description": "Canone Unico Patrimoniale - CORPORATE (0201138TS)",
            "name": "0201138TS"
          }
        ],
        "validity_date": today,
        "due_date": today
      }
}

function buildIbanUpdate() {
    today = new Date()
    today.setYear(today.getYear() + 1)
    return {
        "description": "Updated description testing",
        "iban": "IT99C0222211111000000003333",
        "is_active": true,
        "labels": [
          {
            "description": "Canone Unico Patrimoniale - CORPORATE (0201138TS)",
            "name": "0201138TS"
          }
        ],
        "validity_date": today,
        "due_date": today
      }
}


module.exports = {
    buildIbanCreate,
    buildIbanUpdate
}
