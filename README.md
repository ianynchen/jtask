# JTask

## Description

JTask is a Java based library to simplify concurrent/parallel processing. It's based on project-reactor to provide concurrent/parallel processing. It aims to provide:

1. Allows tasks that depend on each other to be linked to form a chain effect, such that error handling and chain processing can happen automatically without much coding.
2. Allows task processing to be run on separate threads while hiding the details from developers.

## Basic Concepts

### Task

A task can be treated as a mathematical function. It works on some input values, and returns a response. A tasked can have dependency tasks, other tasks that need to be completed before start running this task, to form a chain of tasks.

### Task Aggregator

To form a chain of tasks, output from a task's dependency tasks need to be aggregated before the aggregated input can be fed to the current task as its input. A task aggregator is used to aggregate outputs from a task's dependency tasks.

### Task Processor

A task processor is where the actual processing inside a task happens. 

### Independent Task

An independent task is a task that has no dependencies.

### Collective Task

Sometimes, a job is to process a collection of inputs, each can be run in parallel, then the output from processing each item in the collection need to be aggregated later. A simple example would be given a list of strings, calculate the sum of the length of all the strings in the list. To do this in JTask, each string is fed into an independent task, whose job is to calculate the length of the string. Output from all such jobs can then be passed on to an aggregator to calculate the sum.

### Parallel Task

Sometimes, a job is to work on a single common input, but in order to complete the job, multiple sub-tasks need to be run to generate its own output, then the output is to be aggregated as a single output. A simple example would be given a user context, such as username and account number, one sub-task will use the username to fetch user profile, while another sub-task will use account number to fetch user account information. When both jobs are completed, the user profile and account information will be merged together to form a return value. Since each sub-task can involve a remote call to another service to fetch the information, having the ability to run these tasks in parallel and then merge the output when both results are available will be an advantage over having to run both tasks sequentially. 

Another issue involved in this type of scenario, is if one of the sub-tasks fails, how would one handle the error? For example, in the above scenario, if user profile fetching failed, but account information is available, should the return indicate an error or just return the account information?

Obviously, this depends largely on the requirement. This is why each task has a fail strategy to indicate how its failure should be handled.

### Fail Strategy

Each task, when used as a dependency task by another task, has the option to specify a fail strategy. Currently, fail strategy only takes two values:

* FAIL_ALL, or
* IGNORE

The first one means if this sub-task fails, the task that depends on it should return failure, while the second one means if this sub-task is failed, the task that depends on this task can still carry on with other work.

# Usage


