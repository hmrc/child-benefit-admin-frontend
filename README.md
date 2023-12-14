
# child-benefit-admin-frontend

## OVERVIEW AND RESPONSIBILITY
The child-benefit-admin-frontend service provides monitoring for Child Benefit supplementary data being sent on to SDES for up to 30 days.

It does this by monitoring data in the [child-benefit-service](https://github.com/hmrc/child-benefit-service)'s child-benefits-supplementary-data mongo collection to read the status of files being processed by SDES. Any file can be retried which will send a new upload request for a file that is already being held in temporary object storage.

## ADMIN SERVICE INSTANCES
There exists an instance of the admin service in each environment (pre-production and production). This can be access using LDAP credentials.

| Environment | Link to Service                                               |
|-------------|---------------------------------------------------------------|
| QA          | https://admin.qa.tax.service.gov.uk/child-benefit-admin/      |
| Staging     | https://admin.staging.tax.service.gov.uk/child-benefit-admin/ |
| Production  | https://admin.tax.service.gov.uk/child-benefit-admin          |

## HOW TO RUN THE SERVICE
To start the service locally, make use of Service Manager. The Service itself can be run using the CHILD_BENEFIT_ADMIN_FRONTEND profile.

```> sm2 --start CHILD_BENEFIT_ADMIN_FRONTEND```

## HOW TO TEST
The service itself has a suite of unit tests as a part of the repository. These are located under _./child_benefit_admin_frontend/test_ can be run directly in your IDE or with a unit test running tool of your choice.

## TEAM CHANNEL AND WHO OWNS THE SERVICE
Owning Team: SCA Optimization

Slack Channel: #team-sca-child-benefit

### License
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").