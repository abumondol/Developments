function chunks = find_chunks(labels)
    len = length(labels);
    chunks = [];
    start = 1;
    lb = labels(1);
    for i=2:len
        if labels(i) ~= lb
            chunks = [chunks; start, i-1, lb];
            start = i;
            lb = labels(i);
        end
    end
    
    chunks = [chunks; start, len, lb];
end