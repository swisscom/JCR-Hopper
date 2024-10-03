import React, { FC, createContext } from "react";

import { styled } from "goober";

export const RunEndpointContext = createContext("");

const RootElement = styled("div")`
	height: 100%;
	padding: 0.5em;
	gap: 1em;
	font-size: 12px;
	display: grid;
	grid-template-areas:
		"toolbar toolbar run-controls output-controls"
		"script-editor script-editor arguments arguments"
		"script-editor script-editor output output"
		"parameters parameters output output"
		"log-level param-controls output output";
	grid-template-rows: min-content min-content 1fr min-content min-content;
	grid-template columns: 1fr 1fr 1fr 1fr;

	> .toolbar {
		grid-area: toolbar;
	}
	> .run-controls {
		grid-area: run-controls;
	}
	> .output-controls {
		grid-area: output-controls;
	}
	> .arguments {
		grid-area: arguments;
	}
	> .output {
		grid-area: output;
	}
	> .parameters {
		grid-area: parameters;
	}
	> .log-level {
		grid-area: log-level;
	}
	> .param-controls {
		grid-area: param-controls;
	}
`;

export const App: FC<{ runEndpoint: string }> = (props) => {
	return (
		<React.StrictMode>
			<RunEndpointContext.Provider value={props.runEndpoint}>
				<RootElement>
					<div className="toolbar">
						<button is="coral-button" icon="copy" iconsize="S">
							Copy
						</button>
						<button is="coral-button" icon="paste" iconsize="S">
							Paste
						</button>
					</div>
					<div className="run-controls">
						<coral-checkbox name="commit">Commit Changes</coral-checkbox>
						<button is="coral-button" icon="playCircle" iconsize="S">
							Run
						</button>
					</div>
					<div className="output-controls"></div>
					<div className="script-editor"></div>
					<div className="arguments"></div>
					<div className="output"></div>
					<div className="parameters"></div>
					<div className="log-level"></div>
					<div className="param-controls"></div>
				</RootElement>
			</RunEndpointContext.Provider>
		</React.StrictMode>
	);
};
