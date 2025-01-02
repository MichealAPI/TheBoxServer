#!/bin/bash

# External GitHub repo with HTML and CSS files
HTML_REPO="https://github.com/MichealAPI/TheBox"

# Define base paths
SCRIPT_DIR=$(dirname "$(realpath "$0")")
TEMPLATES_DIR="$SCRIPT_DIR/src/main/resources/templates"
STATIC_DIR="$SCRIPT_DIR/src/main/resources/static"

# Ensure target directories exist
mkdir -p "$TEMPLATES_DIR"
mkdir -p "$STATIC_DIR"

# Clone or pull updates from the external repository
echo "Cloning or updating repository..."
if [ -d "tmp" ]; then
  if [ -d "tmp/.git" ]; then
    # Pull latest changes if repo already cloned
    cd tmp && git pull origin master || { echo "Git pull failed"; exit 1; }
    cd ..
  else
    # Repo exists but is not a valid Git directory; remove and re-clone
    rm -rf tmp
    git clone "$HTML_REPO" tmp || { echo "Git clone failed"; exit 1; }
  fi
else
  # Clone repository if "tmp" directory doesn't exist
  git clone "$HTML_REPO" tmp || { echo "Git clone failed"; exit 1; }
fi

# Move HTML files to templates folder
echo "Copying HTML files to $TEMPLATES_DIR..."
find tmp -name '*.html' -exec cp {} "$TEMPLATES_DIR" \;

# Move static files to the static resources folder
echo "Copying static files to $STATIC_DIR..."
if [ -d "tmp/static" ]; then
  cp -r tmp/static/* "$STATIC_DIR"
else
  echo "No static folder found in the repository."
fi

# Process and patch HTML files to enhance Thymeleaf compatibility
echo "Patching HTML files for Thymeleaf compatibility..."
shopt -s nullglob
html_files=("$TEMPLATES_DIR"/*.html) # Collect all HTML files in templates directory

if [ ${#html_files[@]} -eq 0 ]; then
  echo "No HTML files found to process."
else
  # Changes to apply
  pathRemovalCandidates=('/static' 'static')  # The `/static` keyword to remove
  patches=("src" "href" "action")    # Thymeleaf-supported attributes

  # Iterate over each HTML file and process it
  for file in "${html_files[@]}"; do
    echo "Processing file: $file"

    # Remove `/static` safely (excluding URLs with http/https)
    for candidate in "${pathRemovalCandidates[@]}"; do
      echo "    Removing candidate: $candidate (excluding URLs with http/https)"
      if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS-specific sed
        sed -i '' "/http:\/\/\|https:\/\//!s|$candidate||g" "$file"
      else
        # Linux-specific sed
        sed -i "/http:\/\/\|https:\/\//!s|$candidate||g" "$file"
      fi
    done

    # Remove `.html` file extensions only in paths inside specific attributes
    for patch in "${patches[@]}"; do
      echo "    Patching Thymeleaf attribute: $patch"

      if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS-specific sed
        sed -i '' "/http:\/\/\|https:\/\//!s|$patch=\"\([^\"]*\)\.html\"|$patch=\"\1\"|g" "$file"
      else
        # Linux-specific sed
        sed -i "/http:\/\/\|https:\/\//!s|$patch=\"\([^\"]*\)\.html\"|$patch=\"\1\"|g" "$file"
      fi

      # Convert `src`, `href`, `action` to Thymeleaf attributes
      if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "/http:\/\/\|https:\/\//!s|$patch=\"\([^\"']*\)\"|th:$patch=\"@{\1}\"|g" "$file"
      else
        sed -i "/http:\/\/\|https:\/\//!s|$patch=\"\([^\"']*\)\"|th:$patch=\"@{\1}\"|g" "$file"
      fi
    done
  done
fi

# Clean up temporary folder
rm -rf tmp

echo "All tasks completed successfully!"