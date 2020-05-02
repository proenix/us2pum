<template>
  <div class="contact">
    <SearchBox 
      v-on:searchingContact="searchingContact"
      v-on:changeSort="changeSort">
    </SearchBox>
    <ContactItem
      v-for="contact in contacts" 
      v-bind:key="contact.id"
      v-bind:displayName="contact.name"
      v-bind:id="contact.id"
      v-bind:photo="contact.photo"
      v-bind:lookup_key="contact.lookup_key"
      v-bind:phoneNumbers="contact.numbers"
      v-on:contactClicked="contactClicked">>
    </ContactItem>
  </div>
</template>

<script>
// @ is an alias to /src
import ContactItem from '@/components/ContactItem.vue'
import SearchBox from '@/components/SearchBox.vue'

export default {
  name: 'Contact',
  components: {
    ContactItem,
    SearchBox,
  },
  props: {
    contacts: Array,
    searchString: String,
  },
  methods: {
    searchingContact: function(val) {
      this.$emit('searchingContact', val);
    },
    changeSort: function(val) {
      this.$emit('changeSort', val);
    },
    contactClicked: function(id, lookup_key) {
      this.$emit('contactClicked', id, lookup_key);
    }
  }
}
</script>

<style scoped>
.contact {
  padding: 0.1em;
}
</style>
