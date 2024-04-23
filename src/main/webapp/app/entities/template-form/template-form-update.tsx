import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITemplateForm } from 'app/shared/model/template-form.model';
import { getEntity, updateEntity, createEntity, reset } from './template-form.reducer';

export const TemplateFormUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const templateFormEntity = useAppSelector(state => state.templateForm.entity);
  const loading = useAppSelector(state => state.templateForm.loading);
  const updating = useAppSelector(state => state.templateForm.updating);
  const updateSuccess = useAppSelector(state => state.templateForm.updateSuccess);

  const handleClose = () => {
    navigate('/template-form');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.templateFormID !== undefined && typeof values.templateFormID !== 'number') {
      values.templateFormID = Number(values.templateFormID);
    }

    const entity = {
      ...templateFormEntity,
      ...values,
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
          ...templateFormEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mediformappApp.templateForm.home.createOrEditLabel" data-cy="TemplateFormCreateUpdateHeading">
            Create or edit a Template Form
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="template-form-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Template Form ID"
                id="template-form-templateFormID"
                name="templateFormID"
                data-cy="templateFormID"
                type="text"
              />
              <ValidatedField label="Form Type" id="template-form-formType" name="formType" data-cy="formType" type="text" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/template-form" replace color="info">
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

export default TemplateFormUpdate;
