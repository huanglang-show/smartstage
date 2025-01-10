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
import AppDetailView from "@/views/app/AppDetailView.vue";
import AddQuestionPage from "@/views/add/AddQuestionPage.vue";
import AddScoringResultPage from "@/views/add/AddScoringResultPage.vue";
import AddAppPage from "@/views/add/AddAppPage.vue";
import DoAnswerView from "@/views/answer/DoAnswerView.vue";
import AnswerResultView from "@/views/answer/AnswerResultView.vue";
import MyAnswerView from "@/views/answer/MyAnswerView.vue";
import AccessEnum from "@/access/accessEnum";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/home",
    name: "主页",
    component: HomeView,
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: AddAppPage,
    meta: {
      access: AccessEnum.USER,
    },
  },
  /*创建应用用户，编辑应用，题目，结果*/
  {
    path: "/add/app/:id",
    name: "修改应用",
    props: true,
    component: AddAppPage,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: AddQuestionPage,
    props: true,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  {
    path: "/add/scoring_result/:appId",
    name: "创建评分",
    component: AddScoringResultPage,
    props: true,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情",
    component: AppDetailView,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  /*回答问题*/
  {
    path: "/answer/do/:appId",
    name: "答题",
    component: DoAnswerView,
    props: true,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  /*查看答题结果*/
  {
    path: "/answer/my",
    name: "我的答题",
    component: MyAnswerView,
    meta: {
      access: AccessEnum.USER,
    },
  },
  /*查看答题结果*/
  {
    path: "/answer/result/:id",
    name: "查看答题结果",
    component: AnswerResultView,
    props: true,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },

  /*用户登录，注册*/
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
  /*admin管理*/
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
