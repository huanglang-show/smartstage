import axios from "axios";
import { Message } from "@arco-design/web-vue";

const myAxios = axios.create({
  baseURL: "http://localhost:8101",
  timeout: 60000,
  withCredentials: true,
  // headers: { "X-Custom-Header": "foobar" },
});

// 添加请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    return config;
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    // 获取响应数据
    const { data } = response;
    if (data.code === 40100) {
      // 不是请求用户信息的页面，并且当前不是登录页，则跳转到登陆页面
      if (
        response.request.responseURL.includes("/user/get/login") &&
        !window.location.pathname.includes("/login")
      ) {
        Message.warning("未登录，请重新登录");
        window.location.href = "/user/login?direct=${window.location.href}";
      }
    }
    return response;
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  }
);

export default myAxios;
