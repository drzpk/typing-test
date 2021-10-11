import Vue, {DirectiveOptions} from "vue";

const loadingOverlayTargetClassName = "loading-overlay-target";
const loadingOverlayClassName = "loading-overlay";
const activeStateClassName = "active";
const parentOverlaySearchedClassName = "lot-searched";

function createOverlayElement(): HTMLElement {
    const element = document.createElement("div");
    element.className = loadingOverlayClassName;
    return element;
}

function switchVisibility(target: HTMLElement, visible: boolean) {
    const overlayElement = target.getElementsByClassName(loadingOverlayClassName)[0];
    if (visible) {
        target.classList.add(activeStateClassName);
        overlayElement.classList.add(activeStateClassName);
    } else {
        target.classList.remove(activeStateClassName);
        overlayElement.classList.remove(activeStateClassName);
    }
}

function parentHasOverlay(startingElement: HTMLElement): boolean {
    let current: HTMLElement | null = startingElement.parentElement;
    while (current != null) {
        if (current.classList.contains(loadingOverlayTargetClassName))
            return true;

        current = current.parentElement;
    }

    return false;
}

const options: DirectiveOptions = {
    inserted: (el, binding) => {
        if (parentHasOverlay(el)) {
            // Parent's overlay should also cover this directive's area, so duplication is superfluous
            return;
        }

        el.classList.add(loadingOverlayTargetClassName);
        el.style.position = "relative";
        el.appendChild(createOverlayElement());

        if (binding.value)
            switchVisibility(el, true);
    },
    update: (el, binding) => {
        if (!el.classList.contains(loadingOverlayTargetClassName)) {
            // Directive has been disbled
            return;
        }

        if (!el.classList.contains(parentOverlaySearchedClassName)) {
            el.classList.add(parentOverlaySearchedClassName);

            // Double-check for parent's overlay, because when inserted() method is invoked,
            // the element the directive is bound to may not be attached to DOM yet.
            if (parentHasOverlay(el)) {
                switchVisibility(el, false);
                el.classList.remove(loadingOverlayTargetClassName);
                return;
            }
        }

        if (binding.oldValue !== binding.value)
            switchVisibility(el, !!binding.value);
    }
}

Vue.directive("LoadingOverlay", options);
