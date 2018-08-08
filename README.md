# ld-ads-demo

Reference Implementation for LaunchDarkly Analytics Data Stream Consumer

## Usage 

You must have the Analytics Data Stream enabled in your account in order to
test this software. 

1. A valid `LD_SDK_KEY` exported as an environment variable. This key should 
map back to the specific environment of a specific project that you want to 
get events for. 

We use a Makefile to make it easier to build and run this project. 

### Building 

You can run `make build` to build this project for the first time. 

### Running 

You can run `make run` to run the project. 

You will now see events coming through the system in your console. 

## Future Improvements [WIP]

The goal of this project is to show how to consume the stream and send it 
along to various destinations. Some reference destinations will be included 
in the future. 