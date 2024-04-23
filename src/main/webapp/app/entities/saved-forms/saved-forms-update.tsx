import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IChild } from 'app/shared/model/child.model';
import { getEntities as getChildren } from 'app/entities/child/child.reducer';
import { ITemplateForm } from 'app/shared/model/template-form.model';
import { getEntities as getTemplateForms } from 'app/entities/template-form/template-form.reducer';
import { ISavedForms } from 'app/shared/model/saved-forms.model';
import { getEntity, updateEntity, createEntity, reset } from './saved-forms.reducer';

export const SavedFormsUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const children = useAppSelector(state => state.child.entities);
  const templateForms = useAppSelector(state => state.templateForm.entities);
  const savedFormsEntity = useAppSelector(state => state.savedForms.entity);
  const loading = useAppSelector(state => state.savedForms.loading);
  const updating = useAppSelector(state => state.savedForms.updating);
  const updateSuccess = useAppSelector(state => state.savedForms.updateSuccess);

  const handleClose = () => {
    navigate('/saved-forms');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getChildren({}));
    dispatch(getTemplateForms({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.savedFormID !== undefined && typeof values.savedFormID !== 'number') {
      values.savedFormID = Number(values.savedFormID);
    }
    if (values.formID !== undefined && typeof values.formID !== 'number') {
      values.formID = Number(values.formID);
    }

    const entity = {
      ...savedFormsEntity,
      ...values,
      child: children.find(it => it.id.toString() === values.child?.toString()),
      templateForm: templateForms.find(it => it.id.toString() === values.templateForm?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...savedFormsEntity,
          child: savedFormsEntity?.child?.id,
          templateForm: savedFormsEntity?.templateForm?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mediformappApp.savedForms.home.createOrEditLabel" data-cy="SavedFormsCreateUpdateHeading">
            Create or edit a Saved Forms
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="saved-forms-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Saved Form ID" id="saved-forms-savedFormID" name="savedFormID" data-cy="savedFormID" type="text" />
              <ValidatedField label="Form ID" id="saved-forms-formID" name="formID" data-cy="formID" type="text" />
              <ValidatedField label="Form Type" id="saved-forms-formType" name="formType" data-cy="formType" type="text" />
              <ValidatedField id="saved-forms-child" name="child" data-cy="child" label="Child" type="select">
                <option value="" key="0" />
                {children
                  ? children.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="saved-forms-templateForm" name="templateForm" data-cy="templateForm" label="Template Form" type="select">
                <option value="" key="0" />
                {templateForms
                  ? templateForms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/saved-forms" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SavedFormsUpdate;
