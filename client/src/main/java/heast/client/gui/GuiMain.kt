package heast.client.gui

import heast.client.gui.registry.Colors
import heast.client.gui.scenes.Welcome
import heast.client.gui.windowapi.Window

object GuiMain {

	lateinit var window : Window

	@JvmStatic
	fun initialize() {
		window = Window()
			.withBackground(Colors.primary)
			.withWidth(450)
			.withHeight(610)
			.isResizable(false)
			.isTitleBarHidden(true)
			.isFullWindowContent(true)
			.isWindowTitleVisible(false)
			.isDraggableBody(true)
			.build(Welcome::class)
	}
}