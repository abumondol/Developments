function dist = distance(sign1, sign2, type)

    if type == 1
        [dist, path] = airsign_distance( sign1, sign2 );
    elseif type == 2
        [dist, path] = mauth_distance( sign1, sign2 );
    else
        fprintf('error, distance type greater than 2\n');
    end

end
