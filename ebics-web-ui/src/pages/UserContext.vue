<template>
  <q-page class="justify-evenly">
    <div class="q-pa-md">
      <div class="q-pa-md" style="max-width: 400px">
        <div v-if="userContext">
          <q-list bordered padding>
            <q-item-label header>User Context</q-item-label>

            <q-item clickable v-ripple class="q-my-sm">              
              <q-item-section avatar>
                <q-avatar color="primary" text-color="white">
                  <img src="https://cdn.quasar.dev/img/boy-avatar.png">
                </q-avatar>
              </q-item-section>
              <q-item-section>
                <q-item-label caption>User Name</q-item-label>
                <q-item-label>
                  {{userContext.name}}
                </q-item-label>
              </q-item-section>
              <q-item-section side top>
                <q-btn
                  @click="onLogout"
                  label="Logout"
                  color="primary"
                  flat
                  class="q-ml-sm"
                  icon="undo"
                />
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                <q-item-label caption>Logged at</q-item-label>
                <q-item-label>
                  {{userContext.time}}
                </q-item-label>
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                <q-item-label caption>Authentication type</q-item-label>
                <q-item-label>
                  {{authenticationType}}
                </q-item-label>
              </q-item-section>
            </q-item>

            <q-item v-if="authenticationType == AuthenticationType.SSO && ssoDevOverBasic" clickable v-ripple>
              <q-item-section>
                <q-item-label>SSO via BASIC</q-item-label>
                <q-item-label caption>only for dev purposes</q-item-label>
              </q-item-section>
              <q-item-section side >
                <q-toggle color="blue" disable v-model="ssoDevOverBasic"/>
              </q-item-section>
            </q-item>

            <q-separator spaced></q-separator>
            <q-item-label header>User roles</q-item-label>

            <q-item tag="label" v-ripple>
              <q-item-section side top>
                <q-checkbox v-model="hasRoleGuest" disable></q-checkbox>
              </q-item-section>

              <q-item-section>
                <q-item-label>Guest</q-item-label>
                <q-item-label caption>
                  Guest can use predefined shared EBICS bank connections in order to upload / download files.
                </q-item-label>
              </q-item-section>
            </q-item>

            <q-item tag="label" v-ripple>
              <q-item-section side top>
                <q-checkbox v-model="hasRoleUser" disable></q-checkbox>
              </q-item-section>

              <q-item-section>
                <q-item-label>User</q-item-label>
                <q-item-label caption>
                  As user you can create your own bank connection for predefined banks, and use them in order to upload/download files. You can share such connections to other users as well.
                </q-item-label>
              </q-item-section>
            </q-item>

            <q-item tag="label" v-ripple>
              <q-item-section side top>
                <q-checkbox v-model="hasRoleAdmin" disable></q-checkbox>
              </q-item-section>

              <q-item-section>
                <q-item-label>Admin</q-item-label>
                <q-item-label caption>
                  As administrator you can maintain list of bank available to users, maintain all user bank connections. 
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </div>
        <div v-else>
          <p>No user context available, try to log in</p>
          <q-btn color="primary" to="/login">Login</q-btn>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import useUserContextAPI from 'src/components/user-context';
import { AuthenticationType } from 'components/models/user-context';

export default defineComponent({
  name: 'UserContext',
  data() {
    return {
      //'importing enum' in order to be used in template
      AuthenticationType
    };
  },
  methods: {
    async onLogout() {
      await this.resetUserContextData()
    },
  },

  setup() {
    const { authenticationType, ssoDevOverBasic, basicCredentials, userContext, hasRoleUser, hasRoleGuest, hasRoleAdmin, resetUserContextData, refreshUserContextData } = useUserContextAPI();
    return { authenticationType, ssoDevOverBasic, basicCredentials, userContext, hasRoleUser, hasRoleGuest, hasRoleAdmin, resetUserContextData, refreshUserContextData };
  },
});
</script>
