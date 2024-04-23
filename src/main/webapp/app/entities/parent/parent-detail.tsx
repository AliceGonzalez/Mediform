import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './parent.reducer';

export const ParentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const parentEntity = useAppSelector(state => state.parent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="parentDetailsHeading">Parent</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{parentEntity.id}</dd>
          <dt>
            <span id="parentID">Parent ID</span>
          </dt>
          <dd>{parentEntity.parentID}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{parentEntity.name}</dd>
          <dt>
            <span id="lastName">Last Name</span>
          </dt>
          <dd>{parentEntity.lastName}</dd>
        </dl>
        <Button tag={Link} to="/parent" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/parent/${parentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParentDetail;
