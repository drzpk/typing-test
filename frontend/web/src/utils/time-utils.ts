export function formatDuration(durationSeconds: number): string {
    const pad = (n: number) => n.toString().padStart(2, "0");
    let result = "";

    const hours = Math.floor(durationSeconds / 60 / 60);
    if (hours > 0)
        result += `${pad(hours)}:`;

    const minutes = Math.floor((durationSeconds % (60 * 60)) / 60);
    const seconds = durationSeconds % 60;
    result += `${pad(minutes)}:${pad(seconds)}`;

    return result;
}