import bintray.Plugin._

bintray.Plugin.bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("scala", "jdbc")

bintray.Keys.packageAttributes in bintray.Keys.bintray ~=
  ((_: bintray.AttrMap) ++ Map("website_url" -> Seq(bintry.StringAttr("https://github.com/dnvriend/scala-jdbc")), "github_repo" -> Seq(bintry.StringAttr("https://github.com/dnvriend/scala-jdbc.git")), "issue_tracker_url" -> Seq(bintry.StringAttr("https://github.com/dnvriend/scala-jdbc/issues/"))))
