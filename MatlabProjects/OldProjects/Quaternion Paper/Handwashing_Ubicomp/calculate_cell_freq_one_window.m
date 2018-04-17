function freq = calculate_cell_freq_one_window(ca)
    freq = zeros(162, 1);
    ca_count = length(ca);
    for i=1:ca_count
        ix = ca(i);
        freq(ix) = freq(ix) + 1;
    end

end