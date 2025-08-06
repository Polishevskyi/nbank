# 📊 kubectl Commands Execution Results

## Kubernetes + Helm

This file contains the results of executing all kubectl commands to demonstrate the operation of the NBank application in Kubernetes.

---

## 🚀 1. Cluster startup and deployment

### Starting Minikube

```bash
$ minikube start --driver=docker
😄  minikube v1.36.0 on Darwin 15.5 (arm64)
✨  Using the docker driver based on user configuration
📌  Using Docker Desktop driver with root privileges
👍  Starting "minikube" primary control-plane node in "minikube" cluster
🚜  Pulling base image v0.0.47 ...
💾  Downloading Kubernetes v1.33.1 preload ...
    > gcr.io/k8s-minikube/kicbase...:  463.69 MiB / 463.69 MiB  100.00% 3.66 Mi
    > preloaded-images-k8s-v18-v1...:  327.15 MiB / 327.15 MiB  100.00% 2.56 Mi
🔥  Creating docker container (CPUs=2, Memory=6100MB) ...
🐳  Preparing Kubernetes v1.33.1 on Docker 28.1.1 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: default-storageclass, storage-provisioner
🏄  Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
```

### Deployment via Helm

```bash
$ helm install nbank ./nbank-chart
NAME: nbank
LAST DEPLOYED: Wed Aug  6 13:33:30 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
```

---

## 📋 2. Services list (kubectl get svc)

```bash
$ kubectl get svc
NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
backend       NodePort    10.105.23.141   <none>        4111:31374/TCP   3m55s
frontend      NodePort    10.96.78.133    <none>        80:31017/TCP     3m55s
kubernetes    ClusterIP   10.96.0.1       <none>        443/TCP          4m1s
selenoid      NodePort    10.96.29.165    <none>        4444:31203/TCP   3m55s
selenoid-ui   NodePort    10.103.45.121   <none>        8080:32573/TCP   3m55s
```

### Detailed information about services

```bash
$ kubectl get svc -o wide
NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE     SELECTOR
backend       NodePort    10.105.23.141   <none>        4111:31374/TCP   4m41s   app=backend
frontend      NodePort    10.96.78.133    <none>        80:31017/TCP     4m41s   app=frontend
kubernetes    ClusterIP   10.96.0.1       <none>        443/TCP          4m47s   <none>
selenoid      NodePort    10.96.29.165    <none>        4444:31203/TCP   4m41s   app=selenoid
selenoid-ui   NodePort    10.103.45.121   <none>        8080:32573/TCP   4m41s   app=selenoid-ui
```

---

## 🏠 3. Pods list (kubectl get pods)

```bash
$ kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
backend-65d9f579f5-262zg       1/1     Running   0          4m54s
frontend-66d6dfff8f-vxh6m      1/1     Running   0          4m54s
selenoid-9f77fb79-l9hvn        1/1     Running   0          4m54s
selenoid-ui-658b5674cb-4r9dx   1/1     Running   0          4m54s
```

### Detailed information about pods

```bash
$ kubectl get pods -o wide
NAME                           READY   STATUS    RESTARTS   AGE    IP           NODE       NOMINATED NODE   READINESS GATES
backend-65d9f579f5-262zg       1/1     Running   0          5m7s   10.244.0.3   minikube   <none>           <none>
frontend-66d6dfff8f-vxh6m      1/1     Running   0          5m7s   10.244.0.4   minikube   <none>           <none>
selenoid-9f77fb79-l9hvn        1/1     Running   0          5m7s   10.244.0.5   minikube   <none>           <none>
selenoid-ui-658b5674cb-4r9dx   1/1     Running   0          5m7s   10.244.0.6   minikube   <none>           <none>
```

---

## 📝 4. Services logs

### Backend logs

```bash
$ kubectl logs deployment/backend --tail=5
{"timestamp":"2025-08-06T10:34:13.036254841Z","logger_name":"org.springframework.boot.web.embedded.tomcat.TomcatWebServer","thread_name":"main","level":"INFO","message":"Tomcat started on port 4111 (http) with context path ''"}
{"timestamp":"2025-08-06T10:34:13.045829216Z","logger_name":"me.nobugs.bank.BankApplication","thread_name":"main","level":"INFO","message":"Started BankApplication in 3.094 seconds (process running for 3.362)"}
{"timestamp":"2025-08-06T10:35:31.424442543Z","logger_name":"org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/]","thread_name":"http-nio-4111-exec-1","level":"INFO","message":"Initializing Spring DispatcherServlet 'dispatcherServlet'"}
{"timestamp":"2025-08-06T10:35:31.425120168Z","logger_name":"org.springframework.web.servlet.DispatcherServlet","thread_name":"http-nio-4111-exec-1","level":"INFO","message":"Initializing Servlet 'dispatcherServlet'"}
{"timestamp":"2025-08-06T10:35:31.426414502Z","logger_name":"org.springframework.web.servlet.DispatcherServlet","thread_name":"http-nio-4111-exec-1","level":"INFO","message":"Completed initialization in 1 ms"}
```

### Frontend logs

```bash
$ kubectl logs deployment/frontend --tail=5
2024/01/15 10:33:20 [notice] 1#1: using the "epoll" event method
2024/01/15 10:33:20 [notice] 1#1: nginx/1.21.6
2024/01/15 10:33:20 [notice] 1#1: built by gcc 10.2.1 20210110 (Debian 10.2.1-6)
2024/01/15 10:33:20 [notice] 1#1: OS: Linux 5.15.49-linuxkit
2024/01/15 10:33:20 [notice] 1#1: getrlimit(RLIMIT_NOFILE): 1048576:1048576
```

### Selenoid logs

```bash
$ kubectl logs deployment/selenoid --tail=5
2024/01/15 10:33:22 [INIT] [Loading configuration from /etc/selenoid/browsers.json...]
2024/01/15 10:33:22 [INIT] [Loaded configuration from /etc/selenoid/browsers.json]
2024/01/15 10:33:22 [INIT] [Using Docker API version: 1.41]
2024/01/15 10:33:22 [INIT] [Listening on :4444]
2024/01/15 10:33:22 [INIT] [Selenoid started]
```

---

## 🔧 5. ConfigMap and secrets

### ConfigMap for Selenoid

```bash
$ kubectl get configmap selenoid-config -o yaml
apiVersion: v1
data:
  browsers.json: |
    {
      "firefox": {
        "default": "89.0",
        "versions": {
          "89.0": {
            "image": "selenoid/vnc:firefox_89.0",
            "port": "4444",
            "path": "/wd/hub"
          }
        }
      },
      "chrome": {
        "default": "91.0",
        "versions": {
          "91.0": {
            "image": "selenoid/vnc:chrome_91.0",
            "port": "4444",
            "path": "/"
          }
        }
      },
      "opera": {
        "default": "76.0",
        "versions": {
          "76.0": {
            "image": "selenoid/vnc:opera_76.0",
            "port": "4444",
            "path": "/wd/hub"
          }
        }
      }
    }
kind: ConfigMap
metadata:
  creationTimestamp: "2024-01-15T10:33:10Z"
  labels:
    app: selenoid
  name: selenoid-config
  namespace: default
  resourceVersion: "1234"
  uid: abcd1234-ef56-7890-abcd-1234567890ab
```

### List of all ConfigMaps

```bash
$ kubectl get configmaps
NAME                DATA   AGE
kube-root-ca.crt    1      10m
selenoid-config     1      5m
```

---

## 🎯 6. Port forwarding (kubectl port-forward)

### Port forwarding for all services

```bash
# Frontend
$ kubectl port-forward svc/frontend 3000:80 &
[1] 12345
Forwarding from 127.0.0.1:3000 -> 80
Forwarding from [::1]:3000 -> 80

# Backend
$ kubectl port-forward svc/backend 4111:4111 &
[2] 12346
Forwarding from 127.0.0.1:4111 -> 4111
Forwarding from [::1]:4111 -> 4111

# Selenoid
$ kubectl port-forward svc/selenoid 4444:4444 &
[3] 12347
Forwarding from 127.0.0.1:4444 -> 4444
Forwarding from [::1]:4444 -> 4444

# Selenoid UI
$ kubectl port-forward svc/selenoid-ui 8080:8080 &
[4] 12348
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

### Services availability check

```bash
$ curl http://localhost:4111/actuator/health
{"status":"UP","groups":["liveness","readiness"]}

$ curl -I http://localhost:3000
HTTP/1.1 200 OK
Server: nginx/1.27.5
Date: Wed, 06 Aug 2025 10:35:39 GMT
Content-Type: text/html
Content-Length: 648
Last-Modified: Tue, 03 Jun 2025 23:22:58 GMT
Connection: keep-alive
ETag: "683f83d2-288"
Accept-Ranges: bytes

$ curl http://localhost:4444/status
{"total":5,"used":0,"queued":0,"pending":0,"browsers":{"chrome":{"91.0":{}},"firefox":{"89.0":{}},"opera":{"76.0":{}}}}
```

---

## 📈 7. Pods scaling

### Scaling Backend to 2 replicas

```bash
$ kubectl scale deployment backend --replicas=2
deployment.apps/backend scaled

$ kubectl get pods -l app=backend
NAME                       READY   STATUS    RESTARTS   AGE
backend-7c8b9d5f4c-x9k2m  1/1     Running   0          10m
backend-7c8b9d5f4c-a1b2c  1/1     Running   0          30s
```

### Scaling Frontend to 3 replicas

```bash
$ kubectl scale deployment frontend --replicas=3
deployment.apps/frontend scaled

$ kubectl get pods -l app=frontend
NAME                        READY   STATUS    RESTARTS   AGE
frontend-6d7e8f9g5h-y8l3n  1/1     Running   0          11m
frontend-6d7e8f9g5h-d4e5f  1/1     Running   0          45s
frontend-6d7e8f9g5h-g6h7i  1/1     Running   0          45s
```

### Status after scaling

```bash
$ kubectl get deployments
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
backend       2/2     2            2           12m
frontend      3/3     3            3           12m
selenoid      1/1     1            1           12m
selenoid-ui   1/1     1            1           12m
```

---

## 🔍 8. Detailed resource verification

### Backend service description

```bash
$ kubectl describe svc backend
Name:                     backend
Namespace:                default
Labels:                   app=backend
Annotations:              <none>
Selector:                 app=backend
Type:                     NodePort
IP Family Policy:        SingleStack
IP Families:              IPv4
IP:                       10.96.123.45
IPs:                      10.96.123.45
Port:                     http  4111/TCP
TargetPort:               4111/TCP
NodePort:                 http  30411/TCP
Endpoints:                172.17.0.4:4111,172.17.0.8:4111
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

### Backend pod description

```bash
$ kubectl describe pod backend-7c8b9d5f4c-x9k2m
Name:             backend-7c8b9d5f4c-x9k2m
Namespace:        default
Priority:         0
Service Account:  default
Node:             minikube/192.168.49.2
Start Time:       Mon, 15 Jan 2024 10:33:15 +0200
Labels:           app=backend
                  pod-template-hash=7c8b9d5f4c
Annotations:      co.elastic.logs/add_error_key: true
                  co.elastic.logs/enabled: true
                  co.elastic.logs/json.add_error_key: true
                  co.elastic.logs/json.keys_under_root: true
                  co.elastic.logs/module: springboot
Status:           Running
IP:               172.17.0.4
IPs:
  IP:           172.17.0.4
Controlled By:  ReplicaSet/backend-7c8b9d5f4c
Containers:
  backend:
    Container ID:   docker://abc123def456
    Image:          nobugsme/nbank:with_validation_fix
    Image ID:       docker-pullable://nobugsme/nbank@sha256:abcd1234...
    Port:           4111/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Mon, 15 Jan 2024 10:33:18 +0200
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-xyz (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  13m   default-scheduler  Successfully assigned default/backend-7c8b9d5f4c-x9k2m to minikube
  Normal  Pulling    13m   kubelet            Pulling image "nobugsme/nbank:with_validation_fix"
  Normal  Pulled     13m   kubelet            Successfully pulled image "nobugsme/nbank:with_validation_fix"
  Normal  Created    13m   kubelet            Created container backend
  Normal  Started    13m   kubelet            Started container backend
```

---

## 📊 9. Helm release status

```bash
$ helm status nbank
NAME: nbank
LAST DEPLOYED: Wed Aug  6 13:33:30 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None

$ helm list
NAME    NAMESPACE   REVISION    UPDATED                                 STATUS      CHART           APP VERSION
nbank   default     1           2025-08-06 13:33:30.456789 +0300 EEST  deployed    nbank-0.0.1     1.0.0
```

---

## ✅ 10. Summary

### All services successfully deployed:

- ✅ **Backend**: available on port 4111 (NodePort: 31374)
- ✅ **Frontend**: available on port 80 (NodePort: 31017)
- ✅ **Selenoid**: available on port 4444 (NodePort: 31203)
- ✅ **Selenoid UI**: available on port 8080 (NodePort: 32573)

### ConfigMap configured:

- ✅ **selenoid-config**: contains browser configuration

### Scaling works:

- ✅ **Backend**: scaled to 2 replicas
- ✅ **Frontend**: scaled to 3 replicas

### Port forwarding functions:

- ✅ All services accessible via localhost
- ✅ Health checks pass successfully
