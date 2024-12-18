// @ts-ignore
/* eslint-disable */
import request from "@/request";
import API from "@/api/typings";

/** 创建应用 POST /api/app/add */
export async function addAppUsingPost(
  body: API.AppAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/api/app/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除应用 POST /api/app/delete */
export async function deleteAppUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/app/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新应用 POST /api/app/edit */
export async function editAppUsingPost(
  body: API.AppEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/app/edit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getAppVOById GET /api/app/get/vo */
export async function getAppVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAppVO_>("/api/app/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listAppByPage POST /api/app/list/page */
export async function listAppByPageUsingPost(
  body: API.AppQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAppVO_>("/api/app/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** listAppVOByPage POST /api/app/list/page/vo */
export async function listAppVoByPageUsingPost(
  body: API.AppQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAppVO_>("/api/app/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyAppVOByPage POST /api/app/my/list/page/vo */
export async function listMyAppVoByPageUsingPost(
  body: API.AppQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAppVO_>("/api/app/my/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** offlineApp POST /api/app/offline */
export async function offlineAppUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/app/offline", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 审核应用 POST /api/app/review */
export async function reviewAppUsingPost(
  body: API.AppUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/app/review", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
