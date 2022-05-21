#version 330

struct Color
{
    int hasTexture;
    vec4 color;
    vec4 borderColor;
    float borderWidth;
    vec4 roundness;
    vec4 addColor;
    vec4 mulColor;
    vec4 addColorTransparent;
};

struct Text
{
    int isText;
    vec4 textColor;
};

struct Bounding
{
    float x;
    float y;
    float width;
    float height;
    int visibleOutsideParentBounds;
};

struct Dimensions
{
    float x;
    float y;
    float width;
    float height;
};

in vec2 fragUv;
in vec2 fragImgUv;

uniform vec2 componentSize;
uniform Color color;
uniform Bounding bounding;
uniform Dimensions dimensions;
uniform sampler2D componentTexture;
uniform Text text;
uniform int isGradComp;
uniform vec4 gradCompColor;

out vec4 fragOutColor;

int INSIDE_SHAPE = 0, OUTSIDE_SHAPE = 1, INSIDE_BORDER = 2;
float outsideShapeDistance, insideBorderDistance;
vec2 pixelUv;
vec2 actualPixelUv;
vec2 windowPixel;

vec4 getColor();
bool isInsideBorder();
int isOutsideShape();
bool isOutsideParentBounds();

void main()
{
    pixelUv = fragUv * componentSize;
    actualPixelUv = fragUv * componentSize;
    actualPixelUv.y = pixelUv.y;
    actualPixelUv.x = pixelUv.x;

    windowPixel = vec2(dimensions.x + dimensions.width * fragUv.x, dimensions.y + dimensions.height * fragUv.y);

    if(isOutsideParentBounds()) discard;

    if(text.isText == 1)
    {
        vec4 characterPixel = texture(componentTexture, fragImgUv);
        if(characterPixel.r > 0)
        {
            fragOutColor = characterPixel.r * text.textColor;
        }
        else
            discard;
    }
    else
        fragOutColor = getColor();

    fragOutColor += color.addColorTransparent;

    if(fragOutColor.w != 0)
    {
        fragOutColor += color.addColor;
        fragOutColor *= color.mulColor;
    }
}

bool debug1 = false;
vec4 getColor()
{
    vec4 c;
    int state = isOutsideShape();

    vec4 colorValue = vec4(0);
    if(color.hasTexture == 1) colorValue = texture(componentTexture, fragImgUv);
    else colorValue = color.color;

    if(state == INSIDE_SHAPE)
    {
        c = colorValue;
    }
    if(state == INSIDE_BORDER || isInsideBorder() && state != OUTSIDE_SHAPE)
    {
        //if(insideBorderDistance - 1 < color.borderWidth && state == INSIDE_BORDER)
        if(insideBorderDistance > -1 && insideBorderDistance < 0 && state == INSIDE_BORDER)
        {
            float d = 1 + insideBorderDistance;
            if(color.hasTexture == 1)
                c = vec4(mix(color.borderColor.rgb, colorValue.rgb, d), 1);
            c = vec4(mix(color.borderColor.rgb, colorValue.rgb, d), 1);

            //c = vec4(0, 1 + insideBorderDistance, 0, 1);
        }
        else
            c = color.borderColor;
    }
    if(state == OUTSIDE_SHAPE)
    {
        outsideShapeDistance = abs(outsideShapeDistance);
        if(outsideShapeDistance < 1)
        {
            if(color.borderWidth > 0)
                c = vec4(color.borderColor.xyz, color.borderColor.w - outsideShapeDistance);
            else
                c = vec4(colorValue.xyz, colorValue.w - outsideShapeDistance);
        }
    }
    if(isGradComp == 1)
        return vec4(1 - c.r, 1 - c.g, 1 - c.b, c.a) * gradCompColor;
    return c;
}

bool isOutsideParentBounds()
{
    if(bounding.visibleOutsideParentBounds == 1) return false;
    float minX = bounding.x, maxX = bounding.x + bounding.width;
    float minY = bounding.y, maxY = bounding.y + bounding.height;
    if(windowPixel.x < minX || windowPixel.x > maxX) return true;
    if(windowPixel.y < minY || windowPixel.y > maxY) return true;
    return false;
}

bool isInsideBorder()
{
    return (
    actualPixelUv.x <= color.borderWidth || //left
    actualPixelUv.x >= componentSize.x-color.borderWidth || //right
    actualPixelUv.y <= color.borderWidth || //top
    actualPixelUv.y >= componentSize.y-color.borderWidth); //bottom
}

int isOutsideShape()
{
    bool outsideRoundness = false;
    float ratio = componentSize.x;
    if(componentSize.x > componentSize.y) ratio = componentSize.y;

    //---------top-left-roundness---------//
    float d = length(color.roundness.x - vec2(0));
    float x = d * cos(0.785) * ratio;
    float y = d * sin(0.785) * ratio;
    float r = length(vec2(x, y) - vec2(x, 0));

    float r_Border = r - color.borderWidth;
    if(length(actualPixelUv - vec2(x, y)) > r_Border && actualPixelUv.x < r_Border && actualPixelUv.y < r_Border &&
        length(actualPixelUv - vec2(x, y)) < r && actualPixelUv.x < r && actualPixelUv.y < r)
    {
        insideBorderDistance = r - length(actualPixelUv - vec2(x, y));
        insideBorderDistance = insideBorderDistance - color.borderWidth;
        return INSIDE_BORDER;
    }

    if(length(actualPixelUv - vec2(x, y)) > r && actualPixelUv.x < r && actualPixelUv.y < r)
    {
        outsideShapeDistance = length(actualPixelUv - vec2(x, y)) - r;
        outsideRoundness = true;
    }
    //---------top-left-roundness---------//

    //---------bottom-right-roundness---------//
    d = length(color.roundness.w - vec2(0));
    x = componentSize.x - d * cos(0.785) * ratio;
    y = componentSize.y - d * sin(0.785) * ratio;
    r = length(vec2(x, y) - vec2(x, componentSize.y));

    r_Border = r - color.borderWidth;
    if(length(actualPixelUv - vec2(x, y)) > r_Border && actualPixelUv.x > componentSize.x - r_Border && actualPixelUv.y > componentSize.y - r_Border &&
        length(actualPixelUv - vec2(x, y)) < r && actualPixelUv.x > componentSize.x - r && actualPixelUv.y > componentSize.y - r)
    {
        insideBorderDistance = r - length(actualPixelUv - vec2(x, y));
        insideBorderDistance = insideBorderDistance - color.borderWidth;
        return INSIDE_BORDER;
    }

    //if(length(pixelUv - vec2(x, y)) > r && pixelUv.x > componentSize.x - r && pixelUv.y > componentSize.y - r)
    if(length(actualPixelUv - vec2(x, y)) > r && actualPixelUv.x > componentSize.x - r && actualPixelUv.y > componentSize.y - r)
    {
        outsideShapeDistance = r - length(actualPixelUv - vec2(x, y));
        outsideRoundness = true;
    }
    //---------bottom-right-roundness---------//

    //---------bottom-left-roundness---------//
    d = length(color.roundness.z - vec2(0));
    x = d * cos(0.785) * ratio;
    y = componentSize.y - d * sin(0.785) * ratio;
    r = length(vec2(x, y) - vec2(x, componentSize.y));

    r_Border = r - color.borderWidth;
    if(length(actualPixelUv - vec2(x, y)) > r_Border && actualPixelUv.x < r_Border && actualPixelUv.y > componentSize.y - r_Border &&
        length(actualPixelUv - vec2(x, y)) < r && actualPixelUv.x < r && actualPixelUv.y > componentSize.y - r)
    {
        insideBorderDistance = r - length(actualPixelUv - vec2(x, y));
        insideBorderDistance = insideBorderDistance - color.borderWidth;
        return INSIDE_BORDER;
    }

    if(length(actualPixelUv - vec2(x, y)) > r && actualPixelUv.x < r && actualPixelUv.y > componentSize.y - r)
    {
        outsideShapeDistance = length(actualPixelUv - vec2(x, y)) - r;
        outsideRoundness = true;
    }
    //---------bottom-left-roundness---------//

    //---------top-right-roundness---------//
    d = length(color.roundness.y - vec2(0));
    x = componentSize.x - d * cos(0.785) * ratio;
    y = d * sin(0.785) * ratio;
    r = length(vec2(x, y) - vec2(x, 0));

    r_Border = r - color.borderWidth;
    if(length(actualPixelUv - vec2(x, y)) > r_Border && actualPixelUv.x > componentSize.x - r_Border && actualPixelUv.y < r_Border &&
        length(actualPixelUv - vec2(x, y)) < r && actualPixelUv.x > componentSize.x - r && actualPixelUv.y < r)
    {
        insideBorderDistance = r - length(actualPixelUv - vec2(x, y));
        insideBorderDistance = insideBorderDistance - color.borderWidth;

        return INSIDE_BORDER;
    }


    if(length(actualPixelUv - vec2(x, y)) > r && actualPixelUv.x > componentSize.x - r && actualPixelUv.y < r)
    {
        outsideShapeDistance = length(actualPixelUv - vec2(x, y)) - r;
        outsideRoundness = true;
    }
    //---------top-right-roundness---------//
    if(outsideRoundness) return OUTSIDE_SHAPE;
    return INSIDE_SHAPE;
}