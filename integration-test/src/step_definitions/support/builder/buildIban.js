function buildIbanCreate(iban) {
    today = new Date()
    today.setYear(today.getYear() + 1)
    return {
        "description": "Testing",
        "iban": iban,
        "is_active": true,
        "labels": [
          {
            "description": "The iban to use for CUP payments",
            "name": "CUP"
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
            "description": "The iban to use for CUP payments",
            "name": "CUP"
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