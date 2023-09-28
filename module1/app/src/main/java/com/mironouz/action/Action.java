package com.mironouz.action;

public sealed interface Action permits FlyAction, LocateAction, ShootAction { }
