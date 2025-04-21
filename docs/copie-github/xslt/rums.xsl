<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">

  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:template match="/RUMS">
    <html>
      <head> <title>Test affichage RUMs par XSL</title> </head>
      <body>
        <h1>Rums</h1>
        <ul>
          <xsl:apply-templates select="RUM">
            <xsl:sort select="DEUM" />
          </xsl:apply-templates>
        </ul>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="RUM">
    <li>
      <xsl:value-of select="NADL"/><xsl:text>, </xsl:text><xsl:value-of select="DEUM"/><xsl:text>, </xsl:text><xsl:value-of select="DSUM"/>
    </li>
  </xsl:template>

</xsl:stylesheet>