package org.asciidoc.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.asciidoc.intellij.file.AsciiDocFileType;

/**
 * WARNING: instead of this test, consider a golden master test in {@link AsciiDocParserTest}
 * @author yole
 */
public class AsciiDocPsiTest extends LightPlatformCodeInsightFixtureTestCase {
  public void testImageBlockMacro() {
    PsiFile psiFile = configureByAsciiDoc("image::foo.png[Foo]");
    AsciiDocBlockMacro blockMacro = (AsciiDocBlockMacro) psiFile.getChildren()[0];
    assertEquals("image", blockMacro.getMacroName());
    PsiReference[] references = blockMacro.getReferences();
    assertEquals(1, references.length);
  }

  public void testExampleBlock() {
    PsiFile psiFile = configureByAsciiDoc("====\nfoo\n====\n");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(2, children.length);
    assertInstanceOf(children[0], AsciiDocStandardBlock.class);
  }

  public void testExampleBlockWithTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Xyzzy\n====\nfoo\n====\n");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(2, children.length);
    AsciiDocStandardBlock block = (AsciiDocStandardBlock) children[0];
    assertEquals("Xyzzy", block.getTitle());
  }

  public void testImageBlockMacroWithTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Xyzzy\nimage::foo.png[]");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(1, children.length);
    AsciiDocBlockMacro blockMacro = (AsciiDocBlockMacro) children[0];
    assertEquals("image", blockMacro.getMacroName());
    assertEquals("Xyzzy", blockMacro.getTitle());
  }

  public void testIncompleteBlockMacroWithAttributesInNewLine() {
    PsiFile psiFile = configureByAsciiDoc("image::foo.png\n[hi]");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(3, children.length);
    AsciiDocBlockMacro blockMacro = (AsciiDocBlockMacro) children[0];
    assertEquals("image", blockMacro.getMacroName());
    assertEquals("\n", children[1].getText());
    assertEquals("[hi]", children[2].getText());
  }

  public void testBlockAttributes() {
    PsiFile psiFile = configureByAsciiDoc("[NOTE]\n====\nfoo\n====\n");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(2, children.length);
    AsciiDocStandardBlock block = (AsciiDocStandardBlock) children[0];
    assertEquals("NOTE", block.getStyle());
  }


  public void testBlockFollowedBySomethingLookingLikeAHeaderButActuallyPlainText() {
    PsiFile psiFile = configureByAsciiDoc("====\n\n==== Test\n");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(2, children.length);
    AsciiDocStandardBlock block = (AsciiDocStandardBlock) children[0];
    assertEquals("====\n\n==== Test", block.getText());
  }

  public void testSidebarBlockWithTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Xyzzy\n****\nfoo\n****\n");
    PsiElement[] children = psiFile.getChildren();
    assertEquals(2, children.length);
    AsciiDocStandardBlock block = (AsciiDocStandardBlock) children[0];
    assertEquals("Xyzzy", block.getTitle());
  }

  public void testListingBlockAttributesThenSidebar() {
    PsiFile psiFile = configureByAsciiDoc("[source]\n----\nfoo\n----\n<1> Bar\n\n.Title\n****\nFoo\n****\n");
    AsciiDocBlock[] blocks = PsiTreeUtil.getChildrenOfType(psiFile, AsciiDocBlock.class);
    assertNotNull(blocks);
    assertEquals(2, blocks.length);
  }

  public void testListingLanguage() {
    PsiFile psiFile = configureByAsciiDoc("[source,java]\n----\nfoo\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("source-java", listing.getFenceLanguage());
  }

  public void testDiagramLanguage() {
    PsiFile psiFile = configureByAsciiDoc("[plantuml]\n----\nfoo\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("diagram-plantuml", listing.getFenceLanguage());
  }

  public void testDescriptionImagePlain() {
    PsiFile psiFile = configureByAsciiDoc("image::test.adoc[]");
    AsciiDocBlockMacro macro = PsiTreeUtil.getChildOfType(psiFile, AsciiDocBlockMacro.class);
    assertNotNull(macro);
    assertEquals("image", macro.getDescription());
  }

  public void testDescriptionImageWithTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Title\nimage::test.adoc[]");
    AsciiDocBlockMacro macro = PsiTreeUtil.getChildOfType(psiFile, AsciiDocBlockMacro.class);
    assertNotNull(macro);
    assertEquals("Title", macro.getDescription());
  }

  public void testDescriptionListingPlain() {
    PsiFile psiFile = configureByAsciiDoc("----\nListing\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("(Listing)", listing.getDescription());
  }

  public void testDescriptionListingWithAttribute() {
    PsiFile psiFile = configureByAsciiDoc("[source]\n----\nListing\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("[source]", listing.getDescription());
  }

  public void testDescriptionListingWithAttributeAndTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Title\n[source]\n----\nListing\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("[source] Title", listing.getDescription());
  }

  public void testDescriptionListingWithTitle() {
    PsiFile psiFile = configureByAsciiDoc(".Title\n[source]\n----\nListing\n----\n");
    AsciiDocListing listing = PsiTreeUtil.getChildOfType(psiFile, AsciiDocListing.class);
    assertNotNull(listing);
    assertEquals("[source] Title", listing.getDescription());
  }

  public void testDescriptionSection() {
    PsiFile psiFile = configureByAsciiDoc("== Section Title");
    AsciiDocSection section = PsiTreeUtil.getChildOfType(psiFile, AsciiDocSection.class);
    assertNotNull(section);
    assertEquals("Section Title", section.getDescription());
  }

  private PsiFile configureByAsciiDoc(String text) {
    return myFixture.configureByText(AsciiDocFileType.INSTANCE, text);
  }

}
