import {
  removeAllSpecialCharacters,
} from '@/util/stringUtils';

// Unit test for string utils
describe('String Utils', () => {
  it('should remove all special characters', () => {
    const str = "abc's test#s";
    const result = removeAllSpecialCharacters(str);
    expect(result).to.equal("abcs tests");
  });
});
