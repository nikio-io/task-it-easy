import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';

import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';

import Task, { ITask } from './Task';

export interface ITieProp extends StateProps, DispatchProps {}

export class Tie extends React.Component<ITieProp> {
  private tasks: ITask[] = [{ id: 0, name: 'blub' }];

  componentDidMount() {
    this.props.getSession();
  }

  render() {
    return (
      <Row>
        <Col md="9">
          <h2>Welcome to TIE!</h2>

          <h3>Tasks</h3>
          <Task tasks={this.tasks} />
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated
});

const mapDispatchToProps = { getSession };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Tie);
