import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './child-data.reducer';

export const ChildDataDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const childDataEntity = useAppSelector(state => state.childData.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="childDataDetailsHeading">Child Data</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{childDataEntity.id}</dd>
          <dt>
            <span id="childDataID">Child Data ID</span>
          </dt>
          <dd>{childDataEntity.childDataID}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{childDataEntity.name}</dd>
          <dt>
            <span id="lastName">Last Name</span>
          </dt>
          <dd>{childDataEntity.lastName}</dd>
          <dt>
            <span id="dob">Dob</span>
          </dt>
          <dd>{childDataEntity.dob ? <TextFormat value={childDataEntity.dob} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>Child</dt>
          <dd>{childDataEntity.child ? childDataEntity.child.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/child-data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/child-data/${childDataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChildDataDetail;
