function s = get_header(n)
    s = 'a1';
    for i=2:n-1
        s = strcat(s, ',a', num2str(i));
    end
    s = strcat(s, ',class\n');
end