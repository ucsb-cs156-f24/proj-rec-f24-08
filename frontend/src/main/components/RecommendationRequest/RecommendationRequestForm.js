import { Button, Form, Row, Col } from "react-bootstrap";

import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function RecommendationRequestForm({
  initialContents,
  submitAction,
  buttonLabel = "Create",
}) {
  // Stryker disable all
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm({ defaultValues: initialContents || {} });
  // Stryker restore all

  const navigate = useNavigate();

  // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
  // Note that even this complex regex may still need some tweaks

  // Stryker disable Regex

  return (
    <Form onSubmit={handleSubmit(submitAction)}>
      <Row>
        {initialContents && (
          <Col>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="id">Id</Form.Label>
              <Form.Control
                data-testid="RecommendationRequestForm-id"
                id="id"
                type="text"
                {...register("id")}
                value={initialContents.id}
                disabled
              />
            </Form.Group>
          </Col>
        )}

        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professorName">Professor Name</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorName"
              id="professorName"
              type="text"
              isInvalid={Boolean(errors.professorName)}
              {...register("professorName", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorName && "Professor Name is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professorEmail">Professor Email</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorEmail"
              id="professorEmail"
              type="email"
              isInvalid={Boolean(errors.professorEmail)}
              {...register("professorEmail", {
                required: true,
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorEmail && "Professor Email is required. "}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationTypes">
              Recommendation Types
            </Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-recommendationTypes"
              id="recommendationTypes"
              type="string"
              isInvalid={Boolean(errors.recommendationTypes)}
              {...register("recommendationTypes", {})}
            >
              <option value="CS Department BS/MS program">
                CS Department BS/MS program
              </option>
              <option value="Scholarship or Fellowship">
                Scholarship or Fellowship
              </option>
              <option value="MS program (other than CS Dept BS/MS)">
                MS program (other than CS Dept BS/MS)
              </option>
              <option value="PhD program">PhD program</option>
              <option value="Other">Other</option>
            </Form.Select>
          </Form.Group>
        </Col>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="details">Details</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-details"
              id="details"
              type="text"
              isInvalid={Boolean(errors.details)}
              {...register("details")}
            />
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Button type="submit" data-testid="RecommendationRequestForm-submit">
            {buttonLabel}
          </Button>
          <Button
            variant="Secondary"
            onClick={() => navigate(-1)}
            data-testid="RecommendationRequestForm-cancel"
          >
            Cancel
          </Button>
        </Col>
      </Row>
    </Form>
  );
}

export default RecommendationRequestForm;
