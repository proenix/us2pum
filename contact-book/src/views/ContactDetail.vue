<template>
  <div class="contact-details">
    <button v-on:click="backToContactList" class="back">← Go back to Contact List</button>
    <div>
      <h1>{{ currentContact.name }}</h1>
      <div>
        <img class="contact-details-image" v-bind:src="contactPhoto" />
      </div>

      <div class="contact-numbers" v-if="currentContact.numbers.length">
        <h2>Numbers:</h2>
        <ul>
          <li class="number-row" v-for="number in currentContact.numbers" :key="number.number">
            <span class="number-type">{{ number.type }}</span> 
            <span class="number">{{ getNumber(number) }}</span> 
            <button v-on:click="callNumber(getNumber(number))" class="number-call"><img :src="callImg" /></button>
          </li>
        </ul>
      </div>

      <div class="contact-emails" v-if="currentContact.emails.length">
        <h2>Emails:</h2>
        <ul>
          <li class="email-row" v-for="email in currentContact.emails" :key="email.email">
            <span class="email">{{ email.email}}</span> 
            <button v-on:click="sendEmail(email.email)" class="email-call"><img :src="emailImg" /></button>
          </li>
        </ul>
      </div>

      <div class="contact-address" v-if="currentContact.addresses.length">
        <h2>Addresses:</h2>
        <ul>
          <li class="address-row" v-for="address in currentContact.addresses" :key="address.formatted_address">
            {{ address.formatted_address}}
          </li>
        </ul>
      </div>

    </div>
  </div>
</template>

<script>
import call from './../../public/assets/call.png'
import email from './../../public/assets/email.png'
import blank from './../../public/assets/blank.png'

export default {
  name: "ContactDetail",
  data: function() {
    return {
      callImg: call,
      emailImg: email,
      blankImg: blank,
    }
  },
  computed: {
    contactPhoto: function() {
      if (this.currentContact.photo != "") {
        return 'data:image/jpg;base64,' + this.currentContact.photo;
      }
      return blank;
    },
  },
  props: {
    currentContact: Object,
  },
  methods: {
    backToContactList: function() {
      this.$router.push("/");
    },
    getNumber: function(number) {
      if (number.number_normalized != null) {
        return number.number_normalized;
      }
      return number.number;
    },
    callNumber: function(number) {
      try {
        window.AndroidContactHandler.requestContactCall(number);
      } catch (error) {
        console.log("CallNumber: ", number);  
      }
    },
    sendEmail: function(email) {
      try {
        window.AndroidContactHandler.requestContactEmail(email);
      } catch (error) {
        console.log("SendEmail: ", email);  
      }
    }
  }
}
</script>

<style scoped>
.contact-details button {
  font-size: 1em;
  padding: 4px 10px;
  border: 0;
  background: lightblue;
  border-radius: 40px;
}
.contact-details-image {
  width: 100%;
  height: 100%;
}
.contact-numbers ul li, .contact-emails ul li, .contact-address ul li {
  display: block;
}
.contact-numbers ul, .contact-emails ul, .contact-address ul {
  padding: 0;
}
.number-type {
  border-radius: 1em;
  font-size: 0.7em;
  padding: 3px 10px;
  border: 1px solid lightblue;
  background: lightblue;
  font-weight: 700;
  vertical-align: middle;
}
.number {
  vertical-align: middle;
  font-size: 1.2em;
  margin: 0.5em;
}
.contact-details .number-call, .contact-details .email-call {
  vertical-align: middle;
  float: right;
  width: 8em;
  font-size: 12px;
  margin-top: -1em;
  border: none;
  background: lightblue;
  border-radius: 40px;
  height: 40px;
}
.contact-details .number-call img {
  width: 30px;
  height: 30px;
}
.contact-details .email-call img {
  width: 30px;
  height: 20px;
}
.number-row, .email-row, .address-row {
  padding: 1em;;
  background: #f9feff;
  border-radius: 10px;
}
.address-row {
  white-space: pre-line;
}
</style>