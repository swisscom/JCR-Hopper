import { useState } from 'react';

import { createDraft, finishDraft, Objectish, Draft } from 'immer';

interface History<S> {
	prev: S[];
	current: S;
	draft: Draft<S>;
	next: S[];
}

export interface HistoryUpdater<S> {
	current: S;
	draft: Draft<S>;
	commit(): void;
	replace(current: S): void;
	canUndo: boolean;
	undo(): void;
	canRedo: boolean;
	redo(): void;
}

export function useHistoryImmutable<S extends Objectish>(initial: S, changeListener: (value: S) => void): HistoryUpdater<S> {
	const [history, setHistory] = useState<History<S>>({ prev: [], current: initial, draft: createDraft(initial), next: [] });

	function commit() {
		const current = finishDraft(history.draft) as S;
		setHistory({
			prev: [...history.prev, history.current],
			current,
			draft: createDraft(current),
			next: [],
		});
		changeListener(current);
	}

	function replace(current: S) {
		Object.assign(history.draft, current);
		commit();
	}

	function undo() {
		const prev = [...history.prev];
		const current = prev.pop();
		if (!current) {
			// No undo possible
			return;
		}
		setHistory({
			prev,
			current,
			draft: createDraft(current),
			next: [history.current, ...history.next],
		});
		changeListener(current);
	}

	function redo() {
		const next = [...history.next];
		const current = next.shift();
		if (!current) {
			// No redo possible
			return;
		}
		setHistory({
			prev: [...history.prev, history.current],
			current,
			draft: createDraft(current),
			next,
		});
		changeListener(current);
	}

	return {
		current: history.current,
		draft: history.draft,
		commit,
		replace,
		canUndo: Boolean(history.prev.length),
		undo,
		canRedo: Boolean(history.next.length),
		redo,
	};
}
