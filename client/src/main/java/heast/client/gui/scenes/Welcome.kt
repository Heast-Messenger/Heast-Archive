package heast.client.gui.scenes

import heast.client.gui.components.layout.Button
import heast.client.gui.components.layout.Link.linkTo
import heast.client.gui.components.window.Default
import heast.client.gui.components.window.Title
import heast.client.gui.components.window.WindowHeight
import heast.client.gui.cssengine.Align
import heast.client.gui.cssengine.CSSProperty.Companion.css
import heast.client.gui.cssengine.Font
import heast.client.gui.cssengine.Spacing
import heast.client.gui.registry.Colors
import heast.client.gui.registry.Icons
import heast.client.gui.utility.TextExtension.toText
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import kotlin.reflect.KClass

@WindowHeight(520)
object Welcome : Default() {
	override val back : KClass<out Parent>
		get() = Start::class

	override val forward : KClass<out Parent>?
		get() = null

	override val title : Node
		get() = Title()

	override val layout : Node
		get() = VBox().apply {
			this.children.add(
				VBox().apply {
					this.children.add(
						"New to Heast?".toText().apply {
							this.css = listOf(
								Font()
									.family("Poppins")
									.weight(Font.Weight.BOLD)
									.size(Font.Size.SMALL)
									.color(Colors.SECONDARY)
							)
						})

					this.children.add(
						Button.builder()
							.withText("Login")
							.withIcon(Icons.Menu.LOGIN)
							.onClick { println("Logging in") }
							.build()
							.linkTo(Login::class))

					this.css = listOf(
						Align.centerLeft,
						Spacing.`2`)
				})

			this.children.add(
				VBox().apply {
					this.children.add(
						"Already a member?".toText().apply {
							this.css = listOf(
								Font()
									.family("Poppins")
									.weight(Font.Weight.BOLD)
									.size(Font.Size.SMALL)
									.color(Colors.SECONDARY)
							)
						})

					this.children.add(
						Button.builder()
							.withText("Sign Up")
							.withIcon(Icons.Menu.SIGNUP)
							.onClick { println("Signing up") }
							.build()
							.linkTo(Method::class))

					this.css = listOf(
						Align.centerLeft,
						Spacing.`2`)
				})

			this.css = listOf(
				Align.center,
				Spacing.`8`)
	}
}