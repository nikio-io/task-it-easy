import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';
import { IItem, Type } from 'app/shared/model/item.model';
import { IRootState } from 'app/shared/reducers';
import { createEntity } from 'app/entities/item/item.reducer';

export interface IAddTaskProp extends StateProps, DispatchProps {}
export interface IAddTaskState {
  task: IItem;
}

export class AddTask extends React.Component<IAddTaskProp, IAddTaskState> {
  constructor(props) {
    super(props);

    this.state = {
      task: new class implements IItem {
        description: string;
        id: number;
        title: string;
        type: Type;
      }()
    };
  }

  addTask = () => {
    this.state.task.type = Type.TASK;

    this.props.createEntity(this.state.task);
  };

  render() {
    return (
      <Row>
        <Col md="9">
          <form onSubmit={this.addTask}>
            <input type="text" placeholder={'Taskname'} onChange={e => (this.state.task.title = e.target.value)} />
            <button type="submit">Add Task!</button>
          </form>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  updating: storeState.item.updating,
  updateSuccess: storeState.item.updateSuccess
});

const mapDispatchToProps = {
  createEntity
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddTask);
