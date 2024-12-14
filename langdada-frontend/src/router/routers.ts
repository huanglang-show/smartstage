import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UserUpdateView from "@/views/user/UserUpdateView.vue";
import NoAuth from "@/views/NoAuth.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import AdminUserView from "@/views/admin/AdminUserView.vue";
import AdminAppView from "@/views/admin/AdminAppView.vue";
import AdminQuestionView from "@/views/admin/AdminQuestionView.vue";
import AdminScoringView from "@/views/admin/AdminScoringView.vue";
import AdminAnswerView from "@/views/admin/AdminAnswerView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/home",
    name: "主页",
    component: HomeView,
  },
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: AdminAppView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/admin/question",
    name: "问题管理",
    component: AdminQuestionView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/admin/scoring",
    name: "得分管理",
    component: AdminScoringView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/admin/Answer",
    name: "回答管理",
    component: AdminAnswerView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/update/user",
    name: "updateUser",
    component: UserUpdateView,
    meta: {
      hideInMenu: true,
      access: "user",
    },
  },
  {
    path: "/about",
    name: "about",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
    meta: {},
  },
  {
    path: "/noAuth",
    name: "noAuth",
    component: NoAuth,
    meta: {
      hideInMenu: true,
    },
  },
];
export default routes;
