class FileService {
    saveToFile(data: any, filename: string, mimeType: string | undefined = undefined) {
        if (!mimeType)
            mimeType = FileService.mapFileExtensionToType(filename);

        const options: BlobPropertyBag = {
            type: mimeType
        };

        const file = new Blob([data], options);
        const url = URL.createObjectURL(file);

        const a = document.createElement("a");
        a.href = url;
        a.download = filename;

        document.body.appendChild(a);
        a.click();

        setTimeout(() => {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }, 0);
    }

    private static mapFileExtensionToType(filename: string): string {
        const extensionSeparator = filename.lastIndexOf(".");
        if (extensionSeparator == -1)
            throw new Error(`No extension was found in filename '${filename}'.`)

        const extension = filename.substring(extensionSeparator + 1);
        switch (extension.toLowerCase()) {
            case "json":
                return "application/json";
            default:
                throw new Error(`No mime type was found for extension '${extension}'.`)
        }
    }
}

export default new FileService();