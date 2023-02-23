# Front-End development ONLY

If you need to do small FE development, it might be **you do not need to locally run the whole BE** and DB and all of those,
**rather you can re-use some existing environment**.

Only limitation is that through public sites this angular proxy might not allow you to connect, but you can create
a tunnel to those environments.

### OpenShift
* Download and install the `oc` tool based on [instructions](https://openshift-dev.internal.cloudflight.io/command-line-tools)
* re-voke your login token (right corner -> your name -> copy login command) and copy the `oc login ..` command
* login through CMD

Now you can work within our openshift environment.

* select the project (the last one will be remembered, you do not need to do everytime)
  * `oc project 663-ems-staging`
* by `oc get svc` you can list services, where you will find `ems`
* by `oc port-forward svc/ems 8085:8080` you will start the tunnel to your local station
  * this means we set it to be forwarded to `8085`, but feel free to change to whichever port

### Interact
**TODO** but briefly, download the MobaXterm or other tool to connect to VM, login with credentials and set port-forwarding to whichever
environment, just again set the port to 8085

##### Starting
There is [proxy-conf.tunnel.json](/proxy-conf.tunnel.json) set by default to use `8085` port.
* There is run config prepared, so you can easily start `serve tunnel 8085` or manually do `npm run serve:tunnel`
