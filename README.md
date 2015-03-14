Java implementation of Object Recording Pattern which can be used by mocking or undo framework.

# Record on fake

    Recorder<List<String>> recorder = new Recorder(List.class);
    List<String> list = recorder.instance();
    
    list.add("one");
    list.add("two");
    
    // return the List.add(Object) method
    recorder.getRecordings().get(0).getMethod();
    
    List<String> replayList = new ArrayList<String>();
    recorder.replayOn(replayList);
    // now replayList contains ["one", "two"]

# Record on real instance

    List<String> myList = new ArrayList<String>();
    Recorder<List<String>> recorder = new Recorder(List.class, myList);
    List<String> list = recorder.instance();
    
    list.add("one");
    list.add("two");
    // now myList contains ["one", "two"]
    
    List<String> replayList = new ArrayList<String>();
    recorder.replayOn(replayList);
    // now replayList contains ["one", "two"]

# Reverse a recording (AKA undo)

    List<String> myList = new ArrayList<String>();
    Recorder<List<String>> recorder = new Recorder(List.class, myList);
    
    recorder.addReverser(new Reverser<List<String>>() {
        @Override
        public void reverse(final Invocation<List<String>> context) {
            context.getInstance().remove(context.getArguments().get(0));
        }
    }).add(type(String.class));
    
    List<String> list = recorder.instance();
    list.add("one");
    list.add("two");
    // now myList contains ["one", "two"]
    
    recorder.reverseOn(myList); // remove "two" then "one"
    // now myList is empty

# TODOs

Instead of doing method call like this :

    invoke(myClass, "methodName", args);

It would be better to do that (longer but refactorable) :

    MethodCaptor<List> captor = MethodCaptor.methodCaptor(List.class);
    captor.instance().list.add("one");
    captor.getMethod().invoke(myClass, args);

or shorter using threadlocal :

    MethodCaptor.captureMethod(List.class).add("one");
    MethodCaptor.withMethod().invoke(myClass, args);
