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

export function formatTime(timestampSeconds: number): string {
    const date = new Date(timestampSeconds * 1000);
    const dateString = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    const timeString = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    return `${dateString} ${timeString}`;
}