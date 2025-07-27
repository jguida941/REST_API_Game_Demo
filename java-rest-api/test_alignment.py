#!/usr/bin/env python3
"""
Unit tests to verify terminal box alignment
"""
import re
import sys
import os

# Add parent directory to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

# Import the functions from run_server.py
from run_server import visible_length, print_box_line, print_horizontal_border

def strip_ansi(text):
    """Remove ANSI escape sequences from text"""
    ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
    return ansi_escape.sub('', text)

def capture_print_output(func, *args, **kwargs):
    """Capture print output from a function"""
    from io import StringIO
    old_stdout = sys.stdout
    sys.stdout = StringIO()
    func(*args, **kwargs)
    output = sys.stdout.getvalue()
    sys.stdout = old_stdout
    return output.strip()

def test_visible_length():
    """Test that visible_length correctly calculates text length without ANSI codes"""
    print("Testing visible_length function...")
    
    # Test plain text
    assert visible_length("Hello World") == 11, "Plain text failed"
    
    # Test text with color codes
    colored_text = "\033[38;5;196mHello\033[0m World"
    assert visible_length(colored_text) == 11, f"Colored text failed: got {visible_length(colored_text)}"
    
    # Test complex colored text
    complex_text = "  \033[38;5;208m▸\033[0m Username: \033[91madmin\033[0m    \033[38;5;208m▸\033[0m Password: \033[91mpassword\033[0m               "
    expected_len = len("  ▸ Username: admin    ▸ Password: password               ")
    assert visible_length(complex_text) == expected_len, f"Complex text failed: got {visible_length(complex_text)}, expected {expected_len}"
    
    print("✓ visible_length tests passed!")

def test_box_alignment():
    """Test that all box lines have consistent width"""
    print("\nTesting box line alignment...")
    
    test_lines = [
        "",
        "  \033[97mSimply:\033[0m                                                  ",
        "  \033[38;5;208m▸\033[0m Paste into browser: \033[93mlocalhost:8080\033[0m                     ",
        "  \033[91mADMIN ACCESS:\033[0m                                            ",
        "  \033[38;5;208m▸\033[0m Username: \033[91madmin\033[0m    \033[38;5;208m▸\033[0m Password: \033[91mpassword\033[0m               ",
        "  \033[38;5;208m▸\033[0m Main Login Page: \033[93mhttp://localhost:8080/gameusers\033[0m      ",
        "         Press \033[91mCtrl+C\033[0m to terminate server connection        "
    ]
    
    # Test each line
    for i, line_content in enumerate(test_lines):
        output = capture_print_output(print_box_line, line_content)
        stripped = strip_ansi(output)
        
        # Each line should be exactly 62 characters (60 content + 2 borders)
        assert len(stripped) == 62, f"Line {i} has wrong length: {len(stripped)} (expected 62)\nLine: '{stripped}'"
        
        # Check that it starts and ends with ║
        assert stripped[0] == '║', f"Line {i} doesn't start with ║"
        assert stripped[-1] == '║', f"Line {i} doesn't end with ║"
    
    print("✓ Box alignment tests passed!")

def test_horizontal_borders():
    """Test horizontal border consistency"""
    print("\nTesting horizontal borders...")
    
    # Test different border types
    borders = ['top', '═', 'bottom']
    expected_chars = ['╔', '╠', '╚']
    
    for border_type, expected_start in zip(borders, expected_chars):
        output = capture_print_output(print_horizontal_border, border_type)
        stripped = strip_ansi(output)
        
        # Should be 62 characters total
        assert len(stripped) == 62, f"Border '{border_type}' has wrong length: {len(stripped)}"
        
        # Check starting character
        assert stripped[0] == expected_start, f"Border '{border_type}' has wrong start char"
    
    print("✓ Horizontal border tests passed!")

def visual_test():
    """Visual test to see the actual output"""
    print("\n" + "="*60)
    print("VISUAL TEST - Check that all right borders align:")
    print("="*60 + "\n")
    
    # Print a sample box
    print_horizontal_border('top')
    print_box_line("                    \033[38;5;208mLogin Instructions\033[0m                     ")
    print_horizontal_border('═')
    print_box_line("")
    print_box_line("  \033[91mADMIN ACCESS:\033[0m                                            ")
    print_box_line("  \033[38;5;208m▸\033[0m Username: \033[91madmin\033[0m    \033[38;5;208m▸\033[0m Password: \033[91mpassword\033[0m               ")
    print_box_line("")
    print_box_line("  \033[38;5;208m▸\033[0m Main Login Page: \033[93mhttp://localhost:8080/gameusers\033[0m      ")
    print_horizontal_border('bottom')
    
    print("\n✓ Visual test complete - verify right borders form a straight line")

if __name__ == "__main__":
    print("Running alignment tests...\n")
    
    try:
        test_visible_length()
        test_box_alignment()
        test_horizontal_borders()
        visual_test()
        
        print("\n" + "="*60)
        print("✅ ALL TESTS PASSED! The alignment should be perfect now.")
        print("="*60)
        
    except AssertionError as e:
        print(f"\n❌ TEST FAILED: {e}")
        sys.exit(1)