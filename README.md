# RPC Socket Framework (Java)

A lightweight, extensible RPC framework built on top of TCP sockets, designed with clean architecture principles, protocol abstraction, and high testability in mind.

---

## Overview

This project implements a custom TCP-based RPC system with:

* Pluggable protocol layer
* Decoupled transport and application logic
* Connection/session abstraction
* Support for concurrent clients (virtual threads)
* Extensible architecture for future protocols

---

## Architecture

The system is divided into clear layers:

```
Transport Layer   → Handles sockets and connections
Protocol Layer    → Encodes/decodes messages
Application Layer → Business logic (handlers)
```

### Key Components

#### **Server (Transport)**

* Accepts client connections
* Delegates connection handling
* Does NOT know about protocol or message format

#### **Connection (Abstraction)**

* Represents a connection capable of sending/receiving messages
* Hides protocol and stream details

#### **ConnectionFactory**

* Responsible for creating `Connection` instances from sockets
* Decouples server from protocol implementation

#### **NetworkProtocol (Protocol)**

* Defines how messages are serialized/deserialized

#### **ProtocolSession (Protocol Implementation)**

* Bridges raw streams and protocol
* Implements `Connection`

#### **MessageHandler (Application)**

* Handles incoming messages
* Contains business logic only

---

## Project Structure

```
org.gbl
├── transport
│   ├── server
|   ├── client
│   └── connection
│       ├── Connection
│       └── ConnectionFactory
│
├── protocol
│   ├── NetworkProtocol
│   └── rjcp
│       ├── RJCP
│       ├── ProtocolConnection
│       └── ProtocolConnectionFactory
│
├── application
│   └── MessageHandler
```

---

## How It Works

1. Server accepts a socket connection
2. `ConnectionFactory` creates a `Connection`
3. Server delegates to `MessageHandler`
4. Handler reads/writes messages via `Connection`
5. Protocol handles serialization transparently

---

## Example Flow

```
Client → TCP → Server → Connection → Handler → Connection → TCP → Client
```

---

## Testing Strategy

### Unit Testing

* Handlers are tested using mocked `Connection`
* No sockets required

### Integration Testing

* Full client-server communication using real sockets

### Key Principle

```
Test behavior, not infrastructure
```

---

## Features

* Virtual thread per connection (high scalability)
* Protocol abstraction (plug-and-play)
* Clean separation of concerns
* Testable without network dependencies
* Extensible for new transports/protocols

---

## Example Usage

### Start Server

```java
var server = new RPCServer(sessionFactory, handler);
server.start(8080);
```

---

### Client Request

```java
var client = new RPCClient("localhost", 8080, new RJCP());
var response = client.sendMessage("Hello");
```

---

## Extending the System

### Add a New Protocol

1. Implement `NetworkProtocol`
2. Create a `Connection` implementation
3. Provide a `ConnectionFactory`

No changes required in:

* `Server`
* `MessageHandler`

---

### Add Middleware (Future)

The design allows evolving into:

* Logging interceptors
* Authentication layers
* Metrics collection
* Retry policies

---

## Design Decisions

### Why Connection Abstraction?

Avoids leaking:

* sockets
* streams
* protocol details

---

### Why ConnectionFactory?

Prevents:

```
Server → Protocol coupling
```

Enables:

```
Server → Abstraction → Implementation
```

---

### Why not handle protocol in server?

To enforce:

```
Single Responsibility Principle
```

---

## Future Improvements

* Connection pooling (client-side)
* Backpressure handling
* Async/non-blocking I/O
* Protocol versioning
* Multiplexed requests
* Observability (metrics/tracing)

---

## Key Concepts

* Clean Architecture
* Dependency Inversion Principle (DIP)
* Transport vs Protocol separation
* Message-based communication
* TCP stream framing

---

## Conclusion

This project is a solid foundation for building:

* Custom RPC systems
* High-performance socket services
* Protocol-driven applications

It prioritizes:

```
Clarity > Cleverness
Decoupling > Convenience
Testability > Shortcut hacks
```
