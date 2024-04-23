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
import { IChildData } from 'app/shared/model/child-data.model';
import { getEntity, updateEntity, createEntity, reset } from './child-data.reducer';

export const ChildDataUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const children = useAppSelector(state => state.child.entities);
  const childDataEntity = useAppSelector(state => state.childData.entity);
  const loading = useAppSelector(state => state.childData.loading);
  const updating = useAppSelector(state => state.childData.updating);
  const updateSuccess = useAppSelector(state => state.childData.updateSuccess);

  const handleClose = () => {
    navigate('/child-data');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.childDataID !== undefined && typeof values.childDataID !== 'number') {
      values.childDataID = Number(values.childDataID);
    }

    const entity = {
      ...childDataEntity,
      ...values,
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
          ...childDataEntity,
          child: childDataEntity?.child?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="mediformappApp.childData.home.createOrEditLabel" data-cy="ChildDataCreateUpdateHeading">
            Create or edit a Child Data
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="child-data-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Child Data ID" id="child-data-childDataID" name="childDataID" data-cy="childDataID" type="text" />
              <ValidatedField label="Name" id="child-data-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Last Name" id="child-data-lastName" name="lastName" data-cy="lastName" type="text" />
              <ValidatedField label="Dob" id="child-data-dob" name="dob" data-cy="dob" type="date" />
              <ValidatedField id="child-data-child" name="child" data-cy="child" label="Child" type="select">
                <option value="" key="0" />
                {children
                  ? children.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/child-data" replace color="info">
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

export default ChildDataUpdate;
