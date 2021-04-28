/**
 * Extracts object from given root by path. Example:
 * root: { sub: { abc: "x"} }
 * path: "sub.abc"
 * result: "x"
 */
export function extractFromObject(path: string, root: any | null): any | undefined {
    if (!root)
        return undefined;

    let current = root;
    const parts = path.split(".");

    for (const part of parts) {
        if (current && Object.prototype.hasOwnProperty.call(current, part))
            current = current[part];
        else
            current = undefined;
    }

    return current;
}