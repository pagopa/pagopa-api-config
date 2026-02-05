function makeIdMix(length) {
    let result = '';
    let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

function makeIdNumber(length) {
    let result = '';
    let characters = '0123456789';
    let charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

function getFutureDate(daysFromNow = 30) {
    const today = new Date();
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + daysFromNow);

    const year = futureDate.getFullYear();
    const month = String(futureDate.getMonth() + 1).padStart(2, '0');
    const day = String(futureDate.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}

function replaceDatesInCsv(csvContent) {
    const dateRegex = /\d{4}-\d{2}-\d{2}/g;

    const dateMap = new Map();

    return csvContent.replace(dateRegex, (matchedDate) => {
        if (dateMap.has(matchedDate)) {
            return dateMap.get(matchedDate);
        }

        const dayOffset = dateMap.size * 5 + 30;
        const newDate = getFutureDate(dayOffset);
        dateMap.set(matchedDate, newDate);

        return newDate;
    });
}

module.exports = {
    makeIdMix,
    makeIdNumber,
    getFutureDate,
    replaceDatesInCsv
}