import {Module} from "vuex";
import {RootState} from "@/store/index";

export interface TestState {
    inProgress: boolean;
    activeTest: Test | undefined;

}

export interface Test {
    id: number;
}

export interface ActiveTest extends Test {
    startTime: Date;
    endTime: Date | undefined;

}

const testModule: Module<TestState, RootState> = {
    state: {
        inProgress: false,
        activeTest: undefined
    }


};

export default testModule;