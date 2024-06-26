import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', meta: { label: 'User Context' }, component: () => import('pages/UserContext.vue') },
      { path: '/banks', meta: { label: 'Banks' }, component: () => import('pages/Banks.vue') },
      {
        path: '/bank',
        name: 'bank/create',
        meta: { label: 'Banks / Create' },
        component: () => import('pages/Bank.vue'),
      },
      {
        path: '/bank/:id',
        name: 'bank/edit',
        meta: { label: 'Banks / Edit' },
        component: () => import('pages/Bank.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id };
        },
      },
      { path: '/templates', meta: { label: 'Templates' }, component: () => import('pages/UploadTemplates.vue') },
      {
        path: '/template',
        name: 'template/create',
        meta: { label: 'Templates / Create' },
        component: () => import('pages/UploadTemplate.vue'),
        props: () => {
          return { action: 'create' };
        },
      },
      {
        path: '/template/:id',
        name: 'template/edit',
        meta: { label: 'Templates / Edit' },
        component: () => import('pages/UploadTemplate.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id, action: 'edit' };
        },
      },
      {
        path: '/template/:id',
        name: 'template/copy',
        meta: { label: 'Templates / Copy' },
        component: () => import('pages/UploadTemplate.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id, action: 'copy' };
        },
      },
      {
        path: '/template/:id',
        name: 'template/view',
        meta: { label: 'Templates / View' },
        component: () => import('pages/UploadTemplate.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id, action: 'view' };
        },
      },
      { path: '/bankconnections', meta: { label: 'Bank connections' }, component: () => import('pages/BankConnections.vue') },
      {
        path: '/bankconnection',
        name: 'bankconnection/create',
        meta: { label: 'Bank connections / Add' },
        component: () => import('src/pages/BankConnection.vue'),
      },
      {
        path: '/bankconnection/:id',
        name: 'bankconnection/edit',
        meta: { label: 'Bank connections / Edit' },
        component: () => import('pages/BankConnection.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id };
        },
      },
      {
        path: '/bankconnection/:id/init',
        name: 'bankconnection/init',
        meta: { label: 'Bank connections / Initialize' },
        component: () => import('pages/BankConnectionInitWizz.vue'),
        props: (route) => {
          const id = Number.parseInt(route.params.id as string, 10);
          if (Number.isNaN(id)) {
            return undefined;
          }
          return { id };
        },
      },
      { 
        path: '/upload/type=:type', name: 'upload', component: () => import('pages/FileUpload.vue'),
        meta: { label: 'Upload File' },
        props: (route) => {
          const fileEditor = route.params.type == 'edit'
          return { fileEditor };
        },
      },
      { path: '/download', meta: { label: 'Download File' }, component: () => import('pages/FileDownload.vue') },
      { path: '/userctx', meta: { label: 'User context' }, component: () => import('pages/UserContext.vue') },
      { path: '/version', meta: { label: 'Version' }, component: () => import('pages/Version.vue') },
      { path: '/settings', meta: { label: 'Settings' }, component: () => import('pages/UserSettings.vue') },
      { path: '/traces', meta: { label: 'Traces' }, component: () => import('pages/Traces.vue') },
      { path: '/userlogin', meta: { label: 'Login' }, component: () => import('pages/UserLogin.vue') },
    ],
  },

  {
    path: '/login',
    meta: { label: 'Login' },
    component: () => import('pages/UserLogin.vue'),
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/Error404.vue'),
  },
];

export default routes;
