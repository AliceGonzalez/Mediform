import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './child-visits.reducer';

export const ChildVisitsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const childVisitsEntity = useAppSelector(state => state.childVisits.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="childVisitsDetailsHeading">Child Visits</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{childVisitsEntity.id}</dd>
          <dt>
            <span id="visitID">Visit ID</span>
          </dt>
          <dd>{childVisitsEntity.visitID}</dd>
          <dt>
            <span id="visitType">Visit Type</span>
          </dt>
          <dd>{childVisitsEntity.visitType}</dd>
          <dt>
            <span id="visitDate">Visit Date</span>
          </dt>
          <dd>
            {childVisitsEntity.visitDate ? (
              <TextFormat value={childVisitsEntity.visitDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Child</dt>
          <dd>{childVisitsEntity.child ? childVisitsEntity.child.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/child-visits" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/child-visits/${childVisitsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChildVisitsDetail;
