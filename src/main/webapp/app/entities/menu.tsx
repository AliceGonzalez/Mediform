import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/login">
        Login
      </MenuItem>
      <MenuItem icon="asterisk" to="/parent">
        Parent
      </MenuItem>
      <MenuItem icon="asterisk" to="/child">
        Child
      </MenuItem>
      <MenuItem icon="asterisk" to="/child-visits">
        Child Visits
      </MenuItem>
      <MenuItem icon="asterisk" to="/template-form">
        Template Form
      </MenuItem>
      <MenuItem icon="asterisk" to="/saved-forms">
        Saved Forms
      </MenuItem>
      <MenuItem icon="asterisk" to="/child-data">
        Child Data
      </MenuItem>
      <MenuItem icon="asterisk" to="/form-status">
        Form Status
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
