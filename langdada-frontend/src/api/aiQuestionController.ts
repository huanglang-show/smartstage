// @ts-ignore
/* eslint-disable */
import request from "@/request";
import API from "@/api/typings";

/** generate POST /api/question/ai/generate */
export async function generateUsingPost(
  body: API.AiGenerateQuestionRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListQuestionFrameWork_>(
    "/api/question/ai/generate",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}
