class DateService {
    formatDateToString(dateObj: Date): string {
        const date = `${dateObj.getFullYear()}-${DateService.pad(dateObj.getMonth() + 1)}-${DateService.pad(dateObj.getDate())}`;
        const time = `${DateService.pad(dateObj.getHours())}:${DateService.pad(dateObj.getMinutes())}`;
        return `${date} ${time}`;
    }

    convertFieldsToDate<T>(object: T, ...fields: Array<string>): T {
        const map = object as any;
        for (let i = 0; i < fields.length; i++) {
            this.convertFieldToDate(map, fields[i], true);
        }
        return object;
    }

    private convertFieldToDate(object: any, fieldPath: string, isRoot: boolean) {
        if (isRoot && Array.isArray(object)) {
            for (let i = 0; i < object.length; i++) {
                this.convertFieldToDate(object[i], fieldPath, false);
            }

            return;
        }

        const parts = fieldPath.split(".");
        const fieldName = parts[0];
        const isTerminal = parts.length == 1;
        const fieldValue = object[fieldName];

        const remainingParts: string | null = !isTerminal ? parts.slice(1, parts.length).join(".") : null;

        if (isTerminal && typeof (fieldValue) === "number") {
            object[fieldName] = new Date(fieldValue * 1000);
        } else if (!isTerminal && Array.isArray(fieldValue)) {
            const array = fieldValue as Array<any>;
            for (let i = 0; i < array.length; i++) {
                this.convertFieldToDate(array[i], remainingParts!, false);
            }
        } else if (!isTerminal && typeof (fieldValue) === "object" && fieldValue !== null) {
            this.convertFieldToDate(fieldValue, remainingParts!, false);
        }
    }

    private static pad(input: number): string {
        return input.toString().padStart(2, "0");
    }
}

export default new DateService();