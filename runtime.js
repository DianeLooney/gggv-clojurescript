const { Client, Server } = require('node-osc');

const client = new Client("127.0.0.1", 4200);

const queue = [];
let inProgress = false;

const send = (...args) => {
	    queue.push(args);

	    sendNowInterval = sendNowInterval || setInterval(sendNow, 10);

	    return null;
}
module.exports.send = send;

let sendNowInterval = null;
const sendNow = () => {
	    if (!queue.length) {
			        clearInterval(sendNowInterval);
			        sendNowInterval = null;
			        return;
			    }
		const val = queue.shift();
	    client.send(...val.map(x => (typeof x === 'object') ? x.name : x)); //queue.shift());
}

const server = new Server(4201, '0.0.0.0');
let forwards = {};
let defaultHandler = msg => msg.slice(1);

server.on('message', (msg) => {
	    const [prefix, unknown, message] = msg;
	    const path = message[0];
	    const destinations = forwards[path];
	    if (!destinations) {
			        return;
			    }
	    destinations.forEach(destination => {
			        const { toSource, toUniform, handler } = destination;
			        const args = handler(message);
			        if (args.length === 3) {
						            send('/source.shader/set/uniform3f', toSource, toUniform, args);
						        } else {
									            send('/source.shader/set/uniform1f', toSource, toUniform, args);
									        }
			    });
})

