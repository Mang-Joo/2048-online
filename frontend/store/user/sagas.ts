import { signIn } from "../../apis/user"
import { signInUserAsync, SIGN_IN_USER_REQUEST } from "../user/actions"
import { call, put, takeEvery } from "redux-saga/effects"

function* signInUserSaga(action: ReturnType<typeof signInUserAsync.request>) {
  try {
    const jwtToken: string = yield call(signIn, action.payload)
    yield put(signInUserAsync.success(jwtToken))
    localStorage.setItem("token", jwtToken)
  } catch (e) {
    yield put(signInUserAsync.failure(e))
  }
}

export function* userSaga() {
  yield takeEvery(SIGN_IN_USER_REQUEST, signInUserSaga)
}
