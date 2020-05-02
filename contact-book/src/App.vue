<template>
  <div id="app">
    <router-view 
      v-bind:contacts="contacts" 
      v-bind:currentContact="currentContact"
      v-on:searchingContact="searchingContact"
      v-on:changeSort="changeSort"
      v-on:contactClicked="contactClicked"
      >
    </router-view>
  </div>
</template>

<script>
export default {
  name: "App",
  data: function() {
    return {
      contacts: [
        {
          id: "1",
          lookup_key: "1",
          name: "Sample",
          photo: "",
          numbers: { number: "+1 234 567 890", },
        },
      ],
      currentContact: {
        id: 1,
        photo: "",
        name: "Sample",
        numbers: [
          { 
            number: "+1 234 567 890",
            number_normalized: "+1234567890",
            type: "Work",
          },
        ],
        emails: [
          { email: "sample@example.com" },
        ],
        addresses: [
          {
            formatted_address: "Street Line1\n Street Line2 \n 12-345 City\n PL",
            city: "City",
            state: "State",
            country: "Poland",
          },
        ],
        
      },
    }
  },
  mounted: function() {
    try {
      window.AndroidContactHandler.requestLoadContacts("");  
    } catch (error) {
      console.log("requestLoadContacts fired.");
    }
  },
  methods: {
    loadContacts: function(json) {
      this.$data.contacts = JSON.parse(json);
    },
    loadContactDetails: function(json) {
      this.$data.currentContact = JSON.parse(json);
    },
    changeSort: function(val) {
      try {
        if (val) {
          window.AndroidContactHandler.requestChangeSort(1);  
        } else {
          window.AndroidContactHandler.requestChangeSort(0);  
        }
      } catch (error) {
        console.log("requestChangeSort fired.");
      }
    },
    searchingContact: function(val) {
      try {
        window.AndroidContactHandler.requestLoadContacts(val);  
      } catch (error) {
        console.log("requestLoadContacts fired.");
      }
    },
    contactClicked: function(id, lookup_key) {
      this.$router.push('contact-detail');
      try {
        window.AndroidContactHandler.requestContactDetails(id, lookup_key);
      } catch (error) {
        console.log('requestContactDetails fired.');
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
