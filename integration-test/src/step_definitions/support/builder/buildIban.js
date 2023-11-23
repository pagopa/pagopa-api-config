function buildIbanCreate(iban) {
    let validityDate = new Date()
    validityDate.setYear(validityDate.getYear() + 1901)
    let dueDate = new Date() 
    dueDate.setYear(dueDate.getYear() + 1902)
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
        "validity_date": validityDate,
        "due_date": dueDate
      }
}

function buildIbanUpdate() {
    let validityDate = new Date()
    validityDate.setYear(validityDate.getYear() + 1901)
    let dueDate = new Date() 
    dueDate.setYear(dueDate.getYear() + 1902)
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
        "validity_date": validityDate,
        "due_date": dueDate
      }
}


module.exports = {
    buildIbanCreate,
    buildIbanUpdate
}
