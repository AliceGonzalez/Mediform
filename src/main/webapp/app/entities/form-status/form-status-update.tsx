import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITemplateForm } from 'app/shared/model/template-form.model';
import { getEntities as getTemplateForms } from 'app/entities/template-form/template-form.reducer';
import { IChild } from 'app/shared/model/child.model';
import { getEntities as getChildren } from 'app/entities/child/child.reducer';
import { IFormStatus } from 'app/shared/model/form-status.model';
import { getEntity, updateEntity, createEntity, reset } from './form-status.reducer';

export const FormStatusUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const templateForms = useAppSelector(state => state.templateForm.entities);
  const children = useAppSelector(state => state.child.entities);
  const formStatusEntity = useAppSelector(state => state.formStatus.entity);
  const loading = useAppSelector(state => state.formStatus.loading);
  const updating = useAppSelector(state => state.formStatus.updating);
  const updateSuccess = useAppSelector(state => state.formStatus.updateSuccess);

  const handleClose = () => {
    navigate('/form-status');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTemplateForms({}));
    dispatch(getChildren({}));
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
    if (values.formStatusID !== undefined && typeof values.formStatusID !== 'number') {
      values.formStatusID = Number(values.formStatusID);
    }

    const entity = {
      ...formStatusEntity,
      ...values,
      templateForm: templateForms.find(it => it.id.toString() === values.templateForm?.toString()),
      child: children.find(it => it.id.toString() === values.child?.toString()),
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
          ...formStatusEntity,
          templateForm: formStatusEntity?.templateForm?.id,
          child: formStatusEntity?.child?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mediformappApp.formStatus.home.createOrEditLabel" data-cy="FormStatusCreateUpdateHeading">
            Create or edit a Form Status
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="form-status-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Form Status ID" id="form-status-formStatusID" name="formStatusID" data-cy="formStatusID" type="text" />
              <ValidatedField label="Status" id="form-status-status" name="status" data-cy="status" type="text" />
              <ValidatedField id="form-status-templateForm" name="templateForm" data-cy="templateForm" label="Template Form" type="select">
                <option value="" key="0" />
                {templateForms
                  ? templateForms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="form-status-child" name="child" data-cy="child" label="Child" type="select">
                <option value="" key="0" />
                {children
                  ? children.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/form-status" replace color="info">
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

export default FormStatusUpdate;
